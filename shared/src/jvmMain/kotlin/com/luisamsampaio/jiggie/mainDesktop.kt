package com.luisamsampaio.jiggie

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Jiggie — dev",
        state = rememberWindowState(width = 390.dp, height = 844.dp)
    ) {
        App()
    }
}