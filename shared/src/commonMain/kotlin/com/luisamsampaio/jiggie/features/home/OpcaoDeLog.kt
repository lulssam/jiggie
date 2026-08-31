package com.luisamsampaio.jiggie.features.home

import androidx.compose.ui.graphics.Color
import com.luisamsampaio.jiggie.features.aplicacao.TipoDeLog

/**
 * Uma opção do menu*/
data class OpcaoDeLog(
    val tipo: TipoDeLog,
    val etiqueta: String,
    val cor: Color
)