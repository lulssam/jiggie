package com.luisamsampaio.jiggie.features.auth

/**
 * Enum que define os tipos de roles que um usuário pode ter.
 */
enum class Role {
    PARENT, CHILD, STAFF
}

/**
 * Classe que define o tipo que a pessoa é. Atribui um id, o nome que vai aparecer
 * na aplicaçao e o role que ela tem.
 * @param id ID da pessoa
 * @param displayName Nome que vai aparecer na aplicação
 * @param role Role que a pessoa tem
 */
enum class UserProfile(val id: String, val displayName: String, val role: Role) {
    CATARINA("catarina", "Catarina", Role.PARENT),
    ZE("ze", "Zé", Role.PARENT),
    LU("lu", "Lu", Role.CHILD),
    PI("pi", "Pi", Role.CHILD),
    ROSA("rosa", "Rosa", Role.STAFF);
}