package com.luisamsampaio.jiggie.features.log.administracao

/**
 * Tudo o que o ecrã AdministracaoMeds precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class AdministracaoMedsUiState(
    val doses: List<DoseDoDia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val gravado: Boolean = false
)

/** Uma linha da folha: um horário de um medicamento, dado ou por dar. */
data class DoseDoDia(
    val medicamentoId: String,
    val nome: String,       // "Carprofen 75mg"
    val hora: String,       // "08:00:00" — o que se envia
    val horaTexto: String,  // "8:00 AM" — o que se mostra
    val dada: Boolean,
)