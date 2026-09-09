package com.luisamsampaio.jiggie.features.log.comida

/**
 * Tudo o que o ecrã Comida precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class ComidaUiState(
    val minutosAtras: Int = 0,
    val quantidade: Double = 0.25,
    val base: String = "",
    val extras: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val gravado: Boolean = false
)