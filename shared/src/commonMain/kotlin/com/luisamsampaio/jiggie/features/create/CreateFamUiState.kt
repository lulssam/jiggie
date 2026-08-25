package com.luisamsampaio.jiggie.features.create

/**
 * Tudo o que o ecrã CreateFam precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class CreateFamUiState(
    val familyName: String = "",
    val yourName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)