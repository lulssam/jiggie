package com.luisamsampaio.jiggie.features.meds

import com.luisamsampaio.jiggie.features.meds.domain.Frequencia
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/**
 * Tudo o que o ecrã Meds precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class MedsUiState(
    val medicamentos: List<MedicamentoUi> = emptyList(),
    val formulario: FormularioUi = FormularioUi(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

/**Um cartão da lista*/
data class MedicamentoUi(
    val id: String,
    val nome: String,
    val detalhe: String,
    val semana: List<DiaUi>,
    val tomas: List<TomaUi>,
    val proxima: String?
)

data class DiaUi(val letra: String, val toca: Boolean, val hoje: Boolean)
data class TomaUi(val hora: LocalTime, val texto: String, val dada: Boolean)

/** As opções do "How often". O formulário precisa delas antes de haver dias escolhidos. */
enum class OpcaoDeFrequencia { Diaria, DiaSimDiaNao, DiasDaSemana }


/**
 * O cartão "ADD MEDICINE", tal como está a ser preenchindo*/
data class FormularioUi(
    val nome: String = "",
    val dose: String = "",
    val opcao: OpcaoDeFrequencia = OpcaoDeFrequencia.Diaria,
    val dias: Set<DayOfWeek> = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
    val vezesPorDia: Int = 2,
    val aGuardar: Boolean = false,
) {
    val podeAdicionar: Boolean
        get() = nome.isNotBlank()
                && dose.isNotBlank()
                && !aGuardar
                && (opcao != OpcaoDeFrequencia.DiasDaSemana || dias.isNotEmpty())

    fun frequencia(): Frequencia = when (opcao) {
        OpcaoDeFrequencia.Diaria -> Frequencia.Diaria
        OpcaoDeFrequencia.DiaSimDiaNao -> Frequencia.DiaSimDiaNao
        OpcaoDeFrequencia.DiasDaSemana -> Frequencia.DiasDaSemana(dias)
    }
}