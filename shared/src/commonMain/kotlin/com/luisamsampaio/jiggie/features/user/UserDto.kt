package com.luisamsampaio.jiggie.features.user

import com.luisamsampaio.jiggie.features.home.FamiliaDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val nome: String,
    val papel: String,
    val familia: FamiliaDto? = null
)

@Serializable
data class  IdDto(
    val id: String
)

@Serializable
data class NomeDto(
    val nome: String
)