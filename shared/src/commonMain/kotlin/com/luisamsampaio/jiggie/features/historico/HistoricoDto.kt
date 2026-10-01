package com.luisamsampaio.jiggie.features.historico

import kotlinx.serialization.Serializable


/**
 * Uma linha da view 'registo' — qualquer um dos 5 tipos.
 *
 * O [tipo] diz quais campos de [dados] vêm preenchidos*/
@Serializable
data class RegistoDto(
    val id: String,
    val tipo: String,
    val quando: String,
    val dados: DadosDoRegisto,
)

/**
 * A parte que muda de tipo para tipo. Tudo tem valor por
 * omissão porque cada tipo só traz as suas chaves — as outras
 * ficam no default.*/
@Serializable
data class DadosDoRegisto(
    // paseio
    val duracao: Int? = null,
    val xixi: Boolean = false,
    val coco: Boolean = false,
    // comida e água
    val quantidade: Double? = null,
    val base: String? = null,
    val extras: List<String> = emptyList(),
    // sintoma
    val sintoma: String? = null,
    val descricao: String? = null,
    val gravidade: Int? = null,
    // medicamento
    val nome: String? = null,
    val dose: String? = null,
)
