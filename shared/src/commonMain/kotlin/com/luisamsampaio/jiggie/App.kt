package com.luisamsampaio.jiggie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.luisamsampaio.jiggie.features.codigo.CodigoFamiliaScreen
import com.luisamsampaio.jiggie.features.create.CreateFamScreen
import com.luisamsampaio.jiggie.features.home.HomeScreen
import com.luisamsampaio.jiggie.features.login.LoginScreen
import com.luisamsampaio.jiggie.features.welcome.WelcomeScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.surface
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.ic_pets
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    JiggieTheme {
        val sessao by supabase.auth.sessionStatus.collectAsState()

        when (sessao) {
            is SessionStatus.Initializing -> EcraDeArranque()

            else -> {
                // Decide-se uma vez só. Quando a pessoa fizer login, o
                // sessionStatus muda para Authenticated — mas a partir daqui
                // quem manda na navegação é o NavController, não este `when`
                val inicio: Any = remember {
                    if (sessao is SessionStatus.Authenticated) Home else Login
                }
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = inicio
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
                            onCriarFamilia = { navController.navigate(Welcome) },
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
                        CodigoFamiliaScreen(
                            nome = dados.nome,
                            codigo = dados.codigo,
                            onEntrar = {
                                navController.navigate(Home) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable<Home> {
                        HomeScreen()
                    }
                }
            }

        }
    }
}

/**
 * O que se vê no meio segundo em que o Supabase está a ler a sessão guardada.
 *
 * De propósito sem indicador de progresso: na maioria dos arranques isto
 * dura menos de um piscar de olhos, e um spinner que aparece e desaparece
 * nesse tempo lê-se como uma falha, não como uma espera.
 */
@Composable
private fun EcraDeArranque() {
    Box(
        modifier = Modifier.fillMaxSize().background(surface),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_pets),
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(40.dp),
        )
    }
}