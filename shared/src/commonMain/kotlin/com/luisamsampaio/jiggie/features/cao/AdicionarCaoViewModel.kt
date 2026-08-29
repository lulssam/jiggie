package com.luisamsampaio.jiggie.features.cao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Gere o estado e a lógica do ecrã AdicionarCao.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class AdicionarCaoViewModel : ViewModel() {

    private val _state = MutableStateFlow(AdicionarCaoUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<AdicionarCaoUiState> = _state.asStateFlow()

    /**
     * Vai buscar os dados ao backend e atualiza o estado do ecrã.
     *
     * Mostra um indicador de carregamento enquanto espera,
     * e um erro se algo correr mal.
     */
    fun gravar() {
        val atual = _state.value
        if (!atual.podeGravar || atual.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                supabase.from("cao").insert(
                    CaoNovo(
                        nome = atual.nome.trim(),
                        raca = atual.raca.trim().ifBlank { null },
                        nascimento = atual.anoNascimento.ifBlank { null }?.let { "$it-01-01" },
                        cor = atual.cor,
                    )
                )

                _state.update { it.copy(isLoading = false, gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("gravar cão falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }
        }
    }

    fun onNome(novo: String) = _state.update { it.copy(nome = novo, error = null) }
    fun onRaca(nova: String) = _state.update { it.copy(raca = nova, error = null) }
    fun onCor(nova: Int) = _state.update { it.copy(cor = nova, error = null) }

    fun onAno(novo: String) {
        val digitos = novo.filter(Char::isDigit).take(4)
        _state.update { it.copy(anoNascimento = digitos, error = null) }
    }

}