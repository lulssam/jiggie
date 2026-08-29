package com.luisamsampaio.jiggie.features.cao

import kotlinx.serialization.Serializable


/**
 * O que se envia para criar um cãe
 *
 * Sem `familia_id`: essa coluna tem `default current_familia_id()` na base
 * de dados, portanto vem da sessão e não do cliente.
 * */
@Serializable
data class CaoNovo(
    val nome: String,
    val raca: String? = null,
    val nascimento: String? = null,
    val cor: Int,
)
