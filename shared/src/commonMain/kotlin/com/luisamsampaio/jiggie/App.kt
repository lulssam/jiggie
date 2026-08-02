package com.luisamsampaio.jiggie

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.luisamsampaio.jiggie.features.login.LoginScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme

@Composable
fun App() {
    JiggieTheme {
        LoginScreen()
    }
}