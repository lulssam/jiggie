package com.luisamsampaio.jiggie.features.meds.data

import com.luisamsampaio.jiggie.features.meds.domain.Frequencia
import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentoNovo
import com.luisamsampaio.jiggie.features.meds.domain.TomaDada
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**Uma linha da tabela `medicamento`, tal como o PostgREST a manda*/
@Serializable
internal data class MedicamentoDto(
    val id: String,
    val nome: String,
    val dose: String,
    val hora: List<String>,
    val frequencia: String,
    val inicio: String,
    val dias: List<Int>

)

/**
 * O que se envia para criar um medicamento
 *
 * Sem valor por omissão de propósito: o supabase-kt não envia
 * campos que estejam no default, e a base ficava sem o default *dela*
 * */
@Serializable
internal data class MedicamentoNovoDto(
    val nome: String,
    val dose: String,
    val hora: List<String>,
    val frequencia: String,
    val inicio: String,
    val dias: List<Int>,
    @SerialName("cao_id") val caoId: String
)

/**Uma toma já dada, como vem da `administracao_medicamento`*/
@Serializable
internal data class TomaDto(
    @SerialName("medicamento_id") val medicamentoId: String,
    @SerialName("hora_prevista") val horaPrevista: String? = null,

    )

/** O que se envia para marcar uma toma*/
@Serializable
internal data class TomaNovaDto(
    @SerialName("medicamento_id") val medicamentoId: String,
    @SerialName("dono_id") val donoId: String,
    @SerialName("hora_prevista") val horaPrevista: String? = null,
    val quantidade: Int
)

/**
 * Conversão entre a base e o dominio. É aqui que existem as palavras
 * "dias_da_semana" e os dias em números ISO
 * */
internal fun MedicamentoDto.paraDominio() = Medicamento(
    id = id,
    nome = nome,
    dose = dose,
    horas = hora.map { LocalTime.parse(it) }.sorted(),
    frequencia = when (frequencia) {
        "diaria" -> Frequencia.Diaria
        "dia_sim_dia_nao" -> Frequencia.DiaSimDiaNao
        "dias_da_semana" -> Frequencia.DiasDaSemana(dias.map { DayOfWeek(it) }.toSet())
        else -> error("Frequência desconhecida: $frequencia")
    },
    inicio = LocalDate.parse(inicio)
)

internal fun MedicamentoNovo.paraDto(caoId: String) = MedicamentoNovoDto(
    nome = nome,
    dose = dose,
    hora = horas.map { it.toString() },
    frequencia = when (frequencia) {
        Frequencia.Diaria -> "diaria"
        Frequencia.DiaSimDiaNao -> "dia_sim_dia_nao"
        is Frequencia.DiasDaSemana -> "dias_da_semana"
    },
    inicio = inicio.toString(),
    dias = (frequencia as? Frequencia.DiasDaSemana)?.dias.orEmpty().map { it.isoDayNumber }
        .sorted(),
    caoId = caoId
)

internal fun TomaDto.paraDominio(): TomaDada? =
    horaPrevista?.let { TomaDada(medicamentoId, LocalTime.parse(it)) }