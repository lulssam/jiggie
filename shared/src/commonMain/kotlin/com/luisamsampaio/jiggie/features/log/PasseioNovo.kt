package com.luisamsampaio.jiggie.features.log

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * O que se envia para registar um passeio.
 *
 * Sem `familia_id`: o trigger `trg_familia` preenche-a a partir do cão, e o
 * cliente nem sequer tem permissão para a enviar. O `dono_id` já tem de ir —
 * é `not null` sem default, e a RLS de insert exige que seja o próprio.
 */
@Serializable
data class PasseioNovo(
    @SerialName("cao_id") val caoId: String,
    @SerialName("dono_id") val donoId: String,
    val duracao: Int,
    val xixi: Boolean,
    val coco: Boolean,
    @SerialName("dh_passeio") val quando: String,
)