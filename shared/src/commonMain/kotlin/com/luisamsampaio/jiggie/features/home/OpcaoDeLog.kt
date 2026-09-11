package com.luisamsampaio.jiggie.features.home

import androidx.compose.ui.graphics.Color
import com.luisamsampaio.jiggie.features.aplicacao.Acao

/**
 * Uma opção do menu*/
data class OpcaoDeLog(
    val tipo: Acao,
    val etiqueta: String,
    val cor: Color
)