package com.luisamsampaio.jiggie

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luisamsampaio.jiggie.features.create.CreateFamScreen
import com.luisamsampaio.jiggie.features.login.LoginScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme

@Composable
fun App(onNavHostReady: suspend (NavController) -> Unit = {}) {
    JiggieTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Login
        ) {
            composable<Login> {
                LoginScreen(
                    onCriarFamilia = { navController.navigate(CriarFamilia) }
                )
            }

            composable<CriarFamilia> {
                CreateFamScreen(
                    onVoltar = {navController.popBackStack()},
                    onContinue = {nomeFam, nomMembro ->
                        // apaga "welcome" e "create" atrás de si: a familia já foi criada
                        navController.navigate(CodigoFamilia(nomeFam, nomMembro)) {
                        }
                    }
                )
            }

            composable<CodigoFamilia> {
                // TODO: mostrar código de convite
            }
        }
    }
}