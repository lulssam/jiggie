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
import com.luisamsampaio.jiggie.features.aplicacao.AplicacaoScreen
import com.luisamsampaio.jiggie.features.aplicacao.AplicacaoViewModel
import com.luisamsampaio.jiggie.features.codigo.CodigoFamiliaScreen
import com.luisamsampaio.jiggie.features.create.CreateFamScreen
import com.luisamsampaio.jiggie.features.home.HomeScreen
import com.luisamsampaio.jiggie.features.login.LoginScreen
import com.luisamsampaio.jiggie.features.welcome.WelcomeScreen
import com.luisamsampaio.jiggie.features.join.JoinFamilyScreen
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.surface
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.ic_pets
import org.jetbrains.compose.resources.painterResource
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun App() {
    JiggieTheme {
        val sessao by supabase.auth.sessionStatus.collectAsState()

        when (sessao) {
            is SessionStatus.Initializing -> EcraDeArranque()

            else -> {
                // Decide-se uma vez só, quando a sessão assenta. Quando a pessoa
                // fizer login o sessionStatus volta a mudar — mas a partir daqui
                // quem manda na navegação é o NavController, não este `when`.
                var inicio by remember { mutableStateOf<Any?>(null) }
                LaunchedEffect(Unit) { inicio = decidirArranque() }

                val destino = inicio
                if (destino == null) {
                    EcraDeArranque()
                } else {
                    Navegacao(inicio = destino)
                }
            }
        }
    }
}

@Composable
private fun Navegacao(inicio: Any) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = inicio
    ) {

        composable<Welcome> {
            WelcomeScreen(
                onCreateFamily = { navController.navigate(CriarFamilia) },
                onJoinCode = { navController.navigate(JuntarFamilia) },
                onLogIn = { navController.navigate(Login) }
            )
        }

        composable<Login> {
            LoginScreen(
                onCriarFamilia = { navController.navigate(Welcome) },
                onEntrou = {
                    navController.navigate(Aplicacao) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable<CriarFamilia> {
            CreateFamScreen(
                onVoltar = { navController.popBackStack() },
                onContinue = { nome, codigo ->
                    // Apaga o formulário atrás de si: a família já foi criada.
                    navController.navigate(CodigoFamilia(nome, codigo)) {
                        popUpTo<CriarFamilia> { inclusive = true }
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
                    navController.navigate(Aplicacao) {
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

        composable<JuntarFamilia> {
            JoinFamilyScreen(
                onBack = { navController.popBackStack() },
                onEntrou = {
                    navController.navigate(Aplicacao) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable<Aplicacao> {
            AplicacaoScreen()
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

/**
 * Decide onde a aplicação abre.
 *
 * São três casos e não dois: estar autenticado não é o mesmo que ter família.
 * Quem se regista e falha logo a seguir a criar ou a entrar numa família — um
 * código de convite mal copiado, a rede a cair a meio — fica exactamente nesse
 * meio-termo. O [Welcome] é o ecrã que lhe dá as duas saídas de volta.
 *
 * @return A rota onde montar o grafo: [Login], [Welcome] ou [Home].
 */
private suspend fun decidirArranque(): Any {
    if (supabase.auth.currentSessionOrNull() == null) return Login

    // O current_familia_id() devolve um escalar: o uuid da família desta
    // pessoa, ou null se ela ainda não pertence a nenhuma.
    val temFamilia = try {
        supabase.postgrest.rpc("current_familia_id").decodeAs<String?>() != null
    } catch (cancelamento: CancellationException) {
        throw cancelamento
    } catch (erro: Exception) {
        println("decidirArranque: não deu para saber a família — $erro")
        true
    }

    return if (temFamilia) Aplicacao else Welcome
}