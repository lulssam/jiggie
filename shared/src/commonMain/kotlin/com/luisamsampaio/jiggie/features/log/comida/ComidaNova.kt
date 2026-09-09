package com.luisamsampaio.jiggie.features.log.comida

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComidaNova(
    @SerialName("cao_id") val caoId: String,
    @SerialName("dono_id") val donoId: String,
    val quantidade: Double,
    val base: String,
    val extras: List<String>,
    @SerialName("dh_comida") val quando: String,
) {
}