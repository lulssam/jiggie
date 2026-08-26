package com.luisamsampaio.jiggie.features.aplicacao

/**
 * Tudo o que o ecrã Aplicacao precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class AplicacaoUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)