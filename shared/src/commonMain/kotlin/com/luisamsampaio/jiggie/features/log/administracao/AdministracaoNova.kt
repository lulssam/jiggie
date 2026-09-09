package com.luisamsampaio.jiggie.features.log.administracao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdministracaoNova(
    @SerialName("medicamento_id") val medicamentoId: String,
    @SerialName("dono_id") val donoId: String,
    val quantidade: Int = 1,
    @SerialName("hora_prevista") val horaPrevista: String,
)
