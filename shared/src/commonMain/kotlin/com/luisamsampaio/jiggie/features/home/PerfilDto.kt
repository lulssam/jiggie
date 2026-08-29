package com.luisamsampaio.jiggie.features.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * O nome da pessoa e o da família dela, numa consulta só.
 *
 * A [familia] é nula de propósito: estar autenticado não é o mesmo que
 * pertencer a uma família.
 */
@Serializable
data class PerfilDto(
    val nome: String,
    val familia: FamiliaDto? = null,
)

@Serializable
data class FamiliaDto(
    val nome: String,
    @SerialName("codigo_convite") val codigoConvite: String
)

@Serializable
data class CaoDto(
    val id: String,
    val nome: String,
    val cor: Int,
    val raca: String? = null,
    val nascimento: String? = null
)