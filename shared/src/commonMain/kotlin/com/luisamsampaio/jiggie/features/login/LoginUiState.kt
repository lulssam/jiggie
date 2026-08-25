package com.luisamsampaio.jiggie.features.login

import com.luisamsampaio.jiggie.emailPlausivel

/**
 * Tudo o que o ecrã login precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 * @property email O que o utilizador já escreveu no campo do email.
 * @property password O que o utilizador já escreveu no campo da palavra-passe.
 * @property sessaoIniciada Passa a true quando o supabase aceitou as credenciais.
 *                          É o sinal para o ecrã sair daqui, quem navega é o ecrã não o vm
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val sessaoIniciada: Boolean = false
) {
    /**
     * Verdadeiro quando já faz sentido deixar a pessoa carregar em "Log in":
     * o email tem um aspeto plausível e a palavra-passe não está vazia.
     *
     * Fica aqui, e não dentro do ecrã, porque decidir se uns dados servem ou
     * não é uma regra da aplicação. O ecrã limita-se a obedecer ao resultado.
     */
    val podeEntrar: Boolean
        get() = emailPlausivel(email) && password.isNotBlank()
}