package com.luisamsampaio.jiggie.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Gere o estado e a lógica do ecrã JoinFamily.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class JoinFamilyViewModel : ViewModel() {

    private val _state = MutableStateFlow(JoinFamilyUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<JoinFamilyUiState> = _state.asStateFlow()

    /**
     * Guarda o código à medida que a pessoa o vai escrevendo.
     * Transforma o em capslock porque o codigo gerado no postgres é
     * gerado em maiusculas*/
    fun onCodigoChange(novo: String) {
        // maiusculas à medida que se escreve
        _state.update { it.copy(codigo = novo.uppercase(), error = null) }
    }

    fun onYourNameChange(novo: String) {
        _state.update { it.copy(yourName = novo, error = null) }
    }

    fun onEmailChange(novo: String) {
        _state.update { it.copy(email = novo, error = null) }
    }

    fun onPasswordChange(nova: String) {
        _state.update { it.copy(password = nova, error = null) }
    }

    /**
     * Função que é chamada quando o utilizador clica no botão "Juntar".
     * Faz a chamada ao backend do supabse
     */
    fun onJuntar() {
        val currentState = _state.value
        if (!currentState.podeJuntar || currentState.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                supabase.auth.signUpWith(Email) {
                    email = currentState.email.trim()
                    password = currentState.password
                    data = buildJsonObject { put("nome", currentState.yourName.trim()) }
                }

                if (supabase.auth.currentSessionOrNull() == null) {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            error = "Check your email to confirm your account, then log in."
                        )
                    }
                    return@launch
                }

                // invocar funçao sql
                supabase.postgrest.rpc(
                    "join_familia",
                    buildJsonObject { put("codigo", currentState.codigo.trim()) }
                )

                _state.update { it.copy(isLoading = false, entrou = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("onJuntar falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }
        }
    }

}