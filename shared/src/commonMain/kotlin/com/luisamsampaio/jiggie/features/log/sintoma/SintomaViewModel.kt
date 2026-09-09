package com.luisamsampaio.jiggie.features.log.sintoma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Gere o estado e a lógica do ecrã Sintoma.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class SintomaViewModel : ViewModel() {

    private val _state = MutableStateFlow(SintomaUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<SintomaUiState> = _state.asStateFlow()

    fun onHora(minutos: Int) {
        _state.update { it.copy(minutosAtras = minutos, error = null) }
    }

    fun onTipo(sintoma: String) {
        _state.update { it.copy(tipo = sintoma, error = null) }
    }

    fun onGravidade(gravidade: Int) {
        _state.update { it.copy(gravidade = gravidade, error = null) }
    }

    fun onDescricao(notas: String) {
        _state.update { it.copy(descricao = notas, error = null) }
    }

    fun reiniciar() {
        _state.value = SintomaUiState()
    }

    /**
     * Vai buscar os dados ao backend e atualiza o estado do ecrã.
     *
     * Mostra um indicador de carregamento enquanto espera,
     * e um erro se algo correr mal.
     */
    fun gravar(caoId: String) {
        val atual = _state.value
        if (atual.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val dono = supabase.auth.currentUserOrNull()?.id
                if (dono == null) {
                    _state.update { it.copy(error = "User not logged in") }
                    return@launch
                }

                supabase.from("sintoma").insert(
                    SintomaNovo(
                        caoId = caoId,
                        donoId = dono,
                        descricao = atual.descricao,
                        quando = (Clock.System.now() - atual.minutosAtras.minutes).toString(),
                        tipo = atual.tipo,
                        gravidade = atual.gravidade
                    )
                )
                _state.update { it.copy(gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }

        }
    }

}