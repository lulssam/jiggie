package com.luisamsampaio.jiggie.features.cao

/**
 * Tudo o que o ecrã AdicionarCao precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class AdicionarCaoUiState(
    val nome: String = "",
    val raca: String = "",
    val anoNascimento: String = "",
    val cor: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val gravado: Boolean = false
) {
    val podeGravar: Boolean
        get() = nome.isNotBlank() && anoPlausivel

    /**
     * Em branco é aceitável — nem toda a gente sabe o ano do seu cão.
     * Como o [emailPlausivel], isto não é validação a sério: só evita
     * mandar disparates para o servidor.
     */
    private val anoPlausivel: Boolean
        get() = anoNascimento.isBlank() || anoNascimento.toIntOrNull()
            ?.let { it in 1990..2100 } == true
}

