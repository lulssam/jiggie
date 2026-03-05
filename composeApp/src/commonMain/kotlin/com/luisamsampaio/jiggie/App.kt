package com.luisamsampaio.jiggie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.luisamsampaio.jiggie.features.auth.LoginViewModel
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.auth.ui.LoginScreen
import com.luisamsampaio.jiggie.features.medication.ui.MedicationScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme

@Composable
@Preview
fun App() {
    JiggieTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var loggedInUser by remember { mutableStateOf<UserProfile?>(null) }

            if (loggedInUser == null) {
                val loginViewModel = remember { LoginViewModel() }
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToApp = { selectedUser ->
                        loggedInUser = selectedUser
                    }
                )
            } else {
                MedicationScreen(
                    user = loggedInUser!!,
                    onLogout = { loggedInUser = null }
                )
            }
        }
    }
}