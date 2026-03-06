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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.luisamsampaio.jiggie.features.charts.ChartsViewModel
import com.luisamsampaio.jiggie.features.charts.ui.ChartsScreen
import com.luisamsampaio.jiggie.features.health.HealthViewModel
import com.luisamsampaio.jiggie.features.health.ui.HealthScreen
import com.luisamsampaio.jiggie.features.walks.WalksViewModel
import com.luisamsampaio.jiggie.features.walks.ui.WalksScreen

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
                val walksViewModel = remember { WalksViewModel() }
                val healthViewModel = remember { HealthViewModel() }
                val chartsViewModel = remember { ChartsViewModel() }



                NavHost(
                    navController = navController,
                    startDestination = "Remédios"
                ) {

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

                    composable("Passeios") {
                        WalksScreen(
                            user = loggedInUser!!,
                            onLogout = { loggedInUser = null },
                            viewModel = walksViewModel,
                            onTabSelected = { novaAba ->
                                navController.navigate(novaAba) {
                                    popUpTo("Remédios") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable("Saúde") {
                        HealthScreen(
                            user = loggedInUser!!,
                            onLogout = { loggedInUser = null },
                            viewModel = healthViewModel,
                            onTabSelected = { novaAba ->
                                navController.navigate(novaAba) {
                                    popUpTo("Remédios") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable("Gráficos") {
                        ChartsScreen(
                            user = loggedInUser!!,
                            onLogout = { loggedInUser = null },
                            viewModel = chartsViewModel,
                            onTabSelected = { novaAba ->
                                navController.navigate(novaAba) {
                                    popUpTo("Remédios") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}