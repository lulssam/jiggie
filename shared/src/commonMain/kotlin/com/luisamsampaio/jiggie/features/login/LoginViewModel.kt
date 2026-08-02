package com.luisamsampaio.jiggie.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

            _state.update { it.copy(isLoading = false) }

        }
    }

    /**
     * Transforma um erro do backend numa frase legível para o utilizador.
     *
     * @param erro O erro devolvido pelo backend.
     * @return Uma mensagem em português para mostrar no ecrã.
     */
    private fun mensagem(erro: Any): String = "Erro desconhecido"
}