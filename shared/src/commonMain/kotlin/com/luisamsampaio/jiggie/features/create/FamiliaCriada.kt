package com.luisamsampaio.jiggie.features.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * O que o `create_familia` devolve: o identificador da família acabada de
 * criar e o código com que os outros membros se juntam a ela.
 *
 * Os nomes vêm em snake_case do Postgres, daí os [SerialName].
 */
@Serializable
data class FamiliaCriada(
    @SerialName("familia_id") val familiaId: String,
    @SerialName("codigo_convite") val codigoConvite: String,
)