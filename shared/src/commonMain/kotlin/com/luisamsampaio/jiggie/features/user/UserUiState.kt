package com.luisamsampaio.jiggie.features.user

/**
 * Tudo o que o ecrã User precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class UserUiState(
    val nome: String = "",
    val email: String = "",
    val papel: String = "",
    val nomeFamilia: String = "",
    val codigoFamilia: String = "",
    val membros: Int = 0,
    val caes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val logout: Boolean = false
)