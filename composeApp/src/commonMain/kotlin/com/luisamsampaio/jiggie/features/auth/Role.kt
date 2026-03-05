package com.luisamsampaio.jiggie.features.auth

/**
 * Classe que define o tipo que a pessoa é. Atribui um id, o nome que vai aparecer
 * na aplicaçao e o role que ela tem.
 * @param id ID da pessoa
 * @param displayName Nome que vai aparecer na aplicação
 * @param role Role que a pessoa tem
 */
data class UserProfile(
    val id: String,
    val displayName: String,
    val role: String = "USER"
    ) 