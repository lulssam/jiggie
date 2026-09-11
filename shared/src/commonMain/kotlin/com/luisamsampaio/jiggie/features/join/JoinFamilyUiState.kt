package com.luisamsampaio.jiggie.features.join

import com.luisamsampaio.jiggie.MINIMO_PASSWORD
import com.luisamsampaio.jiggie.emailPlausivel

/**
 * Tudo o que o ecrã JoinFamily precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property temConta True quando a conta já existe: criada numa tentativa anterior,
 * ou porque a pessoa voltou pelo Welcome já com sessão.
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class JoinFamilyUiState(
    val codigo: String = "",
    val yourName: String = "",
    val email: String = "",
    val password: String = "",
    val temConta: Boolean = false,
    val entrou: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * Com a conta já criada, só falta o codigo. Sem conta, é preciso de tudo*/
    val podeJuntar: Boolean
        get() = codigo.isNotBlank() && (temConta || contaValida)

    private val contaValida: Boolean
        get() = yourName.isNotBlank() &&
                emailPlausivel(email) &&
                password.length >= MINIMO_PASSWORD
}