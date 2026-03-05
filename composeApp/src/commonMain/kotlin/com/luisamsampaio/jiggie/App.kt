package com.luisamsampaio.jiggie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.luisamsampaio.jiggie.features.auth.LoginViewModel
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.auth.ui.LoginScreen
import com.luisamsampaio.jiggie.features.medication.MedicationViewModel
import com.luisamsampaio.jiggie.features.medication.ui.MedicationScreen
import com.luisamsampaio.jiggie.features.water.WaterViewModel
import com.luisamsampaio.jiggie.features.water.ui.WaterScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import androidx.navigation.compose.rememberNavController

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
                val navController = rememberNavController()

                val medicationViewModel = remember { MedicationViewModel() }
                val waterViewModel = remember { WaterViewModel() }

                NavHost(
                    navController = navController,
                    startDestination = "Remédios"
                ) {

                    // 2. Rota "Remédios" (com 'R' maiúsculo e acento)
                    composable("Remédios") {
                        MedicationScreen(
                            user = loggedInUser!!,
                            onLogout = { loggedInUser = null },
                            viewModel = medicationViewModel,
                            onTabSelected = { novaAba ->
                                navController.navigate(novaAba) {
                                    popUpTo("Remédios") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    // 3. Rota "Água" (com 'Á' maiúsculo)
                    composable("Água") {
                        WaterScreen(
                            user = loggedInUser!!,
                            onLogout = { loggedInUser = null },
                            viewModel = waterViewModel,
                            onTabSelected = { novaAba ->
                                navController.navigate(novaAba) {
                                    popUpTo("Remédios") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    // 4. PREVENIR CRASHES: Adicionar as rotas em falta
                    // Assim podes clicar nelas no menu e em vez de crashar, aparece apenas texto
                    composable("Passeios") { AbaEmConstrucao("Passeios", navController) }
                    composable("Saúde") { AbaEmConstrucao("Saúde", navController) }
                    composable("Gráficos") { AbaEmConstrucao("Gráficos", navController) }
                }
            }
        }
    }
}

@Composable
fun AbaEmConstrucao(nomeAba: String, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "A aba '$nomeAba' ainda está em construção 🚧")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("Remédios") {
                    popUpTo("Remédios") { inclusive = true }
                }
            }) {
                Text("Voltar aos Remédios")
            }
        }
    }
}