package com.luisamsampaio.jiggie.features.home

/**
 * Tudo o que o ecrã Home precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property primeiroNome Nome do utilizador logado
 * @property nomeFamilia Nome da família do utilizador logado
 * @property caes Lista de cães
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class HomeUiState(
    val primeiroNome: String = "",
    val nomeFamilia: String? = null,
    val codigoFamilia: String = "",
    val caes: List<CaoDto> = emptyList(),
    val idCaoAtivo: String? = null,
    val estadoDoDia: EstadoDoDia = EstadoDoDia(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val temCaes: Boolean get() = caes.isNotEmpty()
    val caoAtivo: CaoDto? get() = caes.firstOrNull { it.id == idCaoAtivo } ?: caes.firstOrNull()
}