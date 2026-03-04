package com.luisamsampaio.jiggie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.auth.LoginViewModel
import com.luisamsampaio.jiggie.features.auth.ui.LoginScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import org.jetbrains.compose.resources.painterResource

import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    JiggieTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background

        ) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(viewModel = loginViewModel)
        }
    }
}