package com.luisamsampaio.jiggie

import kotlinx.serialization.Serializable

@Serializable
data object Login
@Serializable
data object Welcome
@Serializable
data object CriarFamilia
@Serializable
data object JuntarFamilia
@Serializable
data object Home

/**
 * O ecrã que mostra o código de convite acabado de gerar.
 *
 * Leva os dados consigo em vez de os ir buscar outra vez: quem criou a
 * familia já os tem na mão, e assim este ecrã não precisa de ViewModel.*/
@Serializable
data class CodigoFamilia(val nome: String, val codigo: String)
