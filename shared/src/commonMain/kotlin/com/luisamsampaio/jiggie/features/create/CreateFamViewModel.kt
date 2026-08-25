package com.luisamsampaio.jiggie.features.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.coroutines.cancellation.CancellationException

/**
 * Gere o estado e a lógica do ecrã CreateFam.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class CreateFamViewModel : ViewModel() {

    private val _state = MutableStateFlow(CreateFamUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<CreateFamUiState> = _state.asStateFlow()


    /**
     * Quando se clica no botão para criar a familia.
     * Faz chamada ao backend*/
    fun onCreateFamilia() {
        val currentState = _state.value

        if (!currentState.podeContinuar || currentState.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                supabase.auth.signUpWith(Email) {
                    email = currentState.email.trim()
                    password = currentState.password
                    data = buildJsonObject { put("nome", currentState.yourName.trim()) }
                }
                // O signUpWith devolve o utilizador criado com ou sem confirmação por
                // email. Quem sabe se ficou sessão aberta é o Auth — e é a sessão que
                // o create_familia precisa para o auth.uid() responder.
                if (supabase.auth.currentSessionOrNull() == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Check your email to confirm your account, then log in."
                        )
                    }
                    return@launch
                }

                val familia = supabase.postgrest
                    .rpc("create_familia", buildJsonObject {
                        put("p_nome", currentState.familyName.trim())
                    }).decodeSingle<FamiliaCriada>()

                _state.update { it.copy(isLoading = false, codigoCriado = familia.codigoConvite) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("onCreateFamilia falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }
        }
    }

    /**
     * Quando se troca o nome da familia
     * @param newFamName Novo nome da familia*/
    fun onFamilyNameChange(newFamName: String) {
        _state.update {
            it.copy(
                familyName = newFamName,
                error = null
            )
        }
    }

    /**
     * Quando se troca o nome do membro
     * @param newName Novo nome do membro*/
    fun onYourNameChange(newName: String) {
        _state.update {
            it.copy(
                yourName = newName,
                error = null
            )
        }
    }

    fun onEmailChange(novo: String) {
        _state.update { it.copy(email = novo, error = null) }
    }

    fun onPasswordChange(nova: String) {
        _state.update { it.copy(password = nova, error = null) }
    }
}