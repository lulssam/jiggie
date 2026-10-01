package com.luisamsampaio.jiggie.features.meds.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

/**
 * Um medicamento de um cão e o plano para o dar.
 *
 * É o modelo do domínio: não sabe nada de Supabase nem de Compose, e por
 * isso as regras sobre ele testam-se sem rede e sem ecrã.
 *
 * @property horas As horas de cada toma num dia em que toca, por ordem.
 * @property inicio O primeiro dia do plano. Antes dele, nunca toca.
 */
data class Medicamento(
    val id: String,
    val nome: String,
    val dose: String,
    val horas: List<LocalTime>,
    val frequencia: Frequencia,
    val inicio: LocalDate,
)

/**Medicamento que ainda não existe, logo não tem id*/
data class MedicamentoNovo(
    val nome: String,
    val dose: String,
    val horas: List<LocalTime>,
    val frequencia: Frequencia,
    val inicio: LocalDate
)

/**Com que frequência se dá um medicamento*/
sealed interface Frequencia {
    /**Todos os dias*/
    data object Diaria : Frequencia

    /**Dia sim dia não a contar do inicio do plano*/
    data object DiaSimDiaNao : Frequencia

    data class DiasDaSemana(val dias: Set<DayOfWeek>) : Frequencia {
        init {
            require(dias.isNotEmpty()) { "Choose at least one day." }
        }
    }
}

/**Uma tom dada: qual o medicamento e a que hora do plano corresponde*/
data class TomaDada(val medicamentoId: String, val hora: LocalTime)

/**Se este medicamento tem tomas no [dia] especificado*/
fun Medicamento.tocaEm(dia: LocalDate): Boolean {
    if (dia < inicio) return false
    return when (val f = frequencia) {
        Frequencia.Diaria -> true
        Frequencia.DiaSimDiaNao -> inicio.daysUntil(dia) % 2 == 0
        is Frequencia.DiasDaSemana -> dia.dayOfWeek in f.dias
    }
}

/**
 * O próximo dia com tomas a seguir a [dia].
 *
 * @return Null se não houver nenhum nas duas semanas seguintes — um plano
 *         que só começa daqui a um mês, por exemplo.
 */
fun Medicamento.proximoDiaDepoisDe(dia: LocalDate): LocalDate? =
    (1..14).asSequence()
        .map { dia.plus(it, DateTimeUnit.DAY) }
        .firstOrNull { tocaEm(it) }

/**As horas de cada toma, para 1, 2 ou 3 tomas por dia*/
fun horasPara(vezesPorDia: Int): List<LocalTime> = when (vezesPorDia) {
    1 -> listOf(LocalTime(9, 0))
    2 -> listOf(LocalTime(8, 0), LocalTime(18, 0))
    3 -> listOf(LocalTime(8, 0), LocalTime(14, 0), LocalTime(20, 0))
    else -> throw IllegalArgumentException("Só há horários para 1 a 3 tomas por dia, não $vezesPorDia")
}