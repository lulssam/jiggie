package com.luisamsampaio.jiggie.features.log

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
 * Gere o estado e a lógica do ecrã Passeio.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class PasseioViewModel : ViewModel() {

    private val _state = MutableStateFlow(PasseioUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<PasseioUiState> = _state.asStateFlow()

    fun onHora(minutos: Int) {
        _state.update { it.copy(minutosAtras = minutos, error = null) }
    }

    fun onXixi() {
        _state.update { it.copy(xixi = !it.xixi, error = null) }
    }

    fun onCoco() {
        _state.update { it.copy(coco = !it.coco, error = null) }
    }

    fun onMais() {
        _state.update { it.copy(duracao = it.duracao + 5, error = null) }
    }

    fun onMenos() {
        _state.update { it.copy(duracao = maxOf(0, it.duracao - 5), error = null) }
    }

    fun reiniciar() {
        _state.value = PasseioUiState()
    }

    fun gravar(caoId: String) {
        val atual = _state.value
        if (atual.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val dono = supabase.auth.currentUserOrNull()?.id
                if (dono == null) {
                    _state.update { it.copy(isLoading = false, error = "User not logged in") }
                    return@launch
                }

                supabase.from("passeio").insert(
                    PasseioNovo(
                        caoId = caoId,
                        donoId = dono,
                        duracao = atual.duracao,
                        xixi = atual.xixi,
                        coco = atual.coco,
                        quando = (Clock.System.now() - atual.minutosAtras.minutes).toString()
                    )
                )
                _state.update { it.copy(isLoading = false, gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("gravar passeio falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }

        }
    }
}