package com.luisamsampaio.jiggie

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.luisamsampaio.jiggie.features.create.CreateFamScreen
import com.luisamsampaio.jiggie.features.home.HomeScreen
import com.luisamsampaio.jiggie.features.login.LoginScreen
import com.luisamsampaio.jiggie.features.welcome.WelcomeScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme

@Composable
fun App(onNavHostReady: suspend (NavController) -> Unit = {}) {
    JiggieTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Login
        ) {

            composable<Welcome> {
                WelcomeScreen(
                    onCreateFamily = { navController.navigate(CriarFamilia) },
                    onJoinCode = {/*TODO: adicionar ecra de juntar com codigo*/ },
                    onLogIn = { navController.navigate(Login) }
                )
            }

            composable<Login> {
                LoginScreen(
                    onCriarFamilia = { navController.navigate(CriarFamilia) },
                    onEntrou = {
                        navController.navigate(Home) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }

            composable<CriarFamilia> {
                CreateFamScreen(
                    onVoltar = { navController.popBackStack() },
                    onContinue = { nome, codigo ->
                        // apaga "welcome" e "create" atrás de si: a familia já foi criada
                        navController.navigate(CodigoFamilia(nome, codigo)) {
                            popUpTo<Login> { inclusive = true }
                        }
                    }
                )
            }

            composable<CodigoFamilia> { entrada ->
                val dados = entrada.toRoute<CodigoFamilia>()
                /*CodigoFamiliaScreen(
                    nome = dados.nome,
                    codigo = dados.codigo,
                    onEntrar = {/*todo: entrar na aplicação*/}
                )*/
            }

            composable<Home> {
                HomeScreen()
            }
        }
    }
}