package com.luisamsampaio.jiggie.features.log.sintoma

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SintomaNovo(
    @SerialName("cao_id")val caoId: String,
    @SerialName("dono_id") val donoId: String,
    val descricao: String,
    @SerialName("dh_sintoma") val quando: String,
    val tipo: String,
    val gravidade: Int
) {
}