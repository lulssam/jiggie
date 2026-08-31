package com.luisamsampaio.jiggie.features.home

import io.ktor.util.collections.StringMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PasseioDto(
    val id: String,
    @SerialName("dh_passeio") val quando: String,
    val xixi: Boolean,
    val coco: Boolean,
    val duracao: Int
)

@Serializable
data class ComidaDto(
    val id: String,
    @SerialName("dh_comida") val quando: String,
    val quantidade: Double,
    val base: String,
    val extras: List<String> = emptyList()
)

@Serializable
data class AguaDto(
    val id: String,
    @SerialName("dh_agua") val quando: String,
    val quantidade: Int,
)

@Serializable
data class SintomaDto(
    val id: String,
    @SerialName("dh_sintoma") val quando: String,
    val tipo: String,
    val descricao: String? = null,
    val gravidade: Int,
)

/** O `hora` é a coluna `time[]` — os horários previstos para cada dia. */
@Serializable
data class MedicamentoDto(
    val id: String,
    val nome: String,
    val dose: String,
    val hora: List<String>,
)

/**
 * Uma dose já dada. O [horaPrevista] diz a que horário do [MedicamentoDto.hora]
 * corresponde — é o que permite saber o que falta dar.
 */
@Serializable
data class AdministracaoDto(
    val id: String,
    @SerialName("dh_medicamento") val quando: String,
    @SerialName("medicamento_id") val medicamentoId: String,
    @SerialName("hora_prevista") val horaPrevista: String? = null,
    val medicamento: MedicamentoResumoDto? = null,
)

@Serializable
data class MedicamentoResumoDto(val nome: String, val dose: String)

