package com.luisamsampaio.jiggie.features.log.agua

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AguaNovo(
    @SerialName("cao_id") val caoId: String,
    @SerialName("dono_id") val donoId: String,
    val quantidade: Int,
    @SerialName("dh_agua") val quando: String
)
