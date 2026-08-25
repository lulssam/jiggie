package com.luisamsampaio.jiggie.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.CancellationException

/**
 * Gere o estado e a lógica do ecrã login.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    /**
     * Guarda o email à medida que a pessoa o vai escrevendo.
     *
     * Aproveita para apagar qualquer erro que estivesse no ecrã: se ela está a
     * corrigir o que escreveu, a mensagem antiga já não se aplica.
     *
     * @param novo O texto completo que está agora no campo do email.
     */
    fun onEscreverEmail(novo: String) {
        _state.update { it.copy(email = novo, error = null) }
    }

    /**
     * Guarda a palavra-passe à medida que a pessoa a vai escrevendo.
     *
     * @param nova O texto completo que está agora no campo da palavra-passe.
     */
    fun onEscreverPassword(nova: String) {
        _state.update { it.copy(password = nova, error = null) }
    }

    /**
     * Tenta entrar na aplicação com o email e a palavra-passe escritos.
     *
     * Não faz nada se os dados ainda não servirem, ou se já houver um pedido a
     * decorrer.
     */
    fun login() {
        val atual = _state.value
        if (!atual.podeEntrar || atual.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                supabase.auth.signInWith(Email) {
                    email = atual.email.trim()
                    password = atual.password
                }

                _state.update { it.copy(isLoading = false, sessaoIniciada = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                _state.update { it.copy(isLoading = false, error = mensagem(erro)) }
            }

        }
    }

    /**
     * Transforma um erro do backend numa frase legível para o utilizador.
     *
     * @param erro O erro devolvido pelo backend.
     * @return Uma mensagem em português para mostrar no ecrã.
     */
    private fun mensagem(erro: Throwable): String = when (erro) {
        is AuthRestException -> when (erro.errorCode) {
            AuthErrorCode.InvalidCredentials -> "Email ou palavra-passe errados."
            AuthErrorCode.EmailNotConfirmed -> "Confirma o email antes de entrares."
            AuthErrorCode.UserBanned -> "Esta conta foi suspensa."
            AuthErrorCode.OverRequestRateLimit -> "Demasiadas tentativas. Espera um pouco."
            else -> "Não foi possível entrar. Tenta outra vez."
        }
        is HttpRequestException -> "Sem ligação. Verifica a internet."
        else -> "Não foi possível entrar. Tenta outra vez."
    }
}