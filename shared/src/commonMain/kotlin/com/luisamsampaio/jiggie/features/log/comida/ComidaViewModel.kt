package com.luisamsampaio.jiggie.features.log.comida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Gere o estado e a lógica do ecrã Comida.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class ComidaViewModel : ViewModel() {

    private val _state = MutableStateFlow(ComidaUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<ComidaUiState> = _state.asStateFlow()

    fun onHora(minutos: Int) {
        _state.update { it.copy(minutosAtras = minutos, error = null) }
    }

    fun onMais() {
        _state.update { it.copy(quantidade = it.quantidade + 0.25, error = null) }
    }

    fun onMenos() =
        _state.update { it.copy(quantidade = maxOf(0.25, it.quantidade - 0.25), error = null) }

    fun onBase(base: String) {
        _state.update { it.copy(base = base, error = null) }
    }

    fun onExtras(extra: String) {
        _state.update {
            it.copy(
                extras = if (extra in it.extras) it.extras - extra else it.extras + extra,
                error = null
            )
        }
    }

    fun reiniciar() {
        _state.value = ComidaUiState()
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

                supabase.from("comida").insert(
                    ComidaNova(
                        caoId = caoId,
                        donoId = dono,
                        quantidade = atual.quantidade,
                        quando = (Clock.System.now() - atual.minutosAtras.minutes).toString(),
                        base = atual.base,
                        extras = atual.extras
                    )
                )
                _state.update { it.copy(gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("gravar comida falhou: $erro")
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            } finally {

                _state.update { it.copy(isLoading = false) }
            }

        }
    }
}