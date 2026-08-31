package com.luisamsampaio.jiggie.features.aplicacao

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.luisamsampaio.jiggie.Historico
import com.luisamsampaio.jiggie.Home
import com.luisamsampaio.jiggie.Medicamentos
import com.luisamsampaio.jiggie.Relatorio
import com.luisamsampaio.jiggie.features.cao.AdicionarCaoScreen
import com.luisamsampaio.jiggie.features.historico.HistoricoScreen
import com.luisamsampaio.jiggie.features.home.HomeScreen
import com.luisamsampaio.jiggie.features.home.menu.OpcaoDeLog
import com.luisamsampaio.jiggie.features.meds.MedsScreen
import com.luisamsampaio.jiggie.features.relatorio.RelatorioScreen
import com.luisamsampaio.jiggie.ui.theme.divider
import com.luisamsampaio.jiggie.ui.theme.food
import com.luisamsampaio.jiggie.ui.theme.med
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.symptom
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.walk
import com.luisamsampaio.jiggie.ui.theme.water
import io.github.jan.supabase.realtime.Column
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.close
import jiggie.shared.generated.resources.history
import jiggie.shared.generated.resources.home
import jiggie.shared.generated.resources.meds
import jiggie.shared.generated.resources.plus
import jiggie.shared.generated.resources.summarize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Drawable
import org.jetbrains.skia.FontWeight


/** O que o pop up do log está a mostra. Null quando está fechdo*/
enum class TipoDeLog { Menu, Passeio, Comida, Agua, Sintoma, Medicamento, Cao }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplicacaoScreen() {
    val separadores = rememberNavController()
    var popUp by remember { mutableStateOf<TipoDeLog?>(null) }

    val entrada by separadores.currentBackStackEntryAsState()
    val destino = entrada?.destination

    var versaoDosCaes by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = surface,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            BarraDeBaixo(
                activo = when {
                    destino?.hasRoute<Home>() == true -> Home
                    destino?.hasRoute<Historico>() == true -> Historico
                    destino?.hasRoute<Medicamentos>() == true -> Medicamentos
                    destino?.hasRoute<Relatorio>() == true -> Relatorio
                    else -> null
                },
                onSeparador = { separadores.irPara(it) },
                onLog = { popUp = TipoDeLog.Menu }
            )
        }
    ) { espaco ->
        NavHost(
            navController = separadores,
            startDestination = Home,
            modifier = Modifier.padding(espaco)
        ) {
            composable<Home> {
                HomeScreen(
                    versao = versaoDosCaes,
                    onAdicionarCao = { popUp = TipoDeLog.Cao },
                    onMedicamentos = { separadores.irPara(Medicamentos) },
                    onHistorico = { separadores.irPara(Historico) }
                )
            }
            composable<Historico> { HistoricoScreen() }
            composable<Medicamentos> { MedsScreen() }
            composable<Relatorio> { RelatorioScreen() }
        }
    }

    popUp?.let { tipo ->
        ModalBottomSheet(
            onDismissRequest = { popUp = null },
            containerColor = surface,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tituloDoPopUp(tipo),
                        style = MaterialTheme.typography.titleMedium,
                        color = textStrong
                    )
                    Icon(
                        painter = painterResource(Res.drawable.close),
                        contentDescription = "Fechar",
                        tint = textDisabled,
                        modifier = Modifier.size(20.dp).clickable { popUp = null }
                    )
                }

                when (tipo) {
                    TipoDeLog.Menu -> MenuDeLog(onEscolha = { popUp = it })

                    TipoDeLog.Cao -> AdicionarCaoScreen(
                        onGravado = {
                            popUp = null
                            versaoDosCaes++
                        }
                    )

                    else -> Text("Por fazer $tipo")
                }
            }
        }
    }
}

/**
 * A barra de separadores.
 *
 * Não conhece navegação: recebe qual está activo e avisa quando se toca
 * num. É isso que a torna pré-visualizável.
 *
 * @param activo A rota do separador actual, ou null se for nenhum deles.
 */
@Composable
fun BarraDeBaixo(
    activo: Any?,
    onSeparador: (Any) -> Unit,
    onLog: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().background(surface)) {
        HorizontalDivider(thickness = 1.dp, color = divider)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, top = 9.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Separador(
                Res.drawable.home, "Home",
                activo == Home
            ) { onSeparador(Home) }

            Separador(
                Res.drawable.history, "History",
                activo == Historico
            ) { onSeparador(Historico) }

            BotaoDeLog(onLog)

            Separador(
                Res.drawable.meds, "Meds",
                activo == Medicamentos
            ) { onSeparador(Medicamentos) }

            Separador(
                Res.drawable.summarize, "Vet",
                activo == Relatorio
            ) { onSeparador(Relatorio) }
        }
    }
}

@Composable
fun BotaoDeLog(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .offset(y = (-14).dp)
            .size(46.dp)
            .clip(CircleShape)
            .background(primaryDark)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.plus),
            contentDescription = "Log",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun Separador(
    icone: DrawableResource,
    etiqueta: String,
    ativo: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val cor = if (ativo) primary else textDisabled
        Icon(
            painter = painterResource(icone),
            contentDescription = etiqueta,
            tint = cor,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = cor
        )
    }
}

/** Salta para um separador sem empilhar histórico. */
private fun NavController.irPara(rota: Any) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Os títulos vêm do TITLES do desenho. */
private fun tituloDoPopUp(tipo: TipoDeLog): String = when (tipo) {
    TipoDeLog.Menu -> "Quick log"
    TipoDeLog.Passeio -> "Log walk"
    TipoDeLog.Comida -> "Log food"
    TipoDeLog.Agua -> "Log water"
    TipoDeLog.Medicamento -> "Give medicine"
    TipoDeLog.Sintoma -> "Log symptom"
    TipoDeLog.Cao -> "Add a dog"
}

private val opcoesDeLog = listOf(
    OpcaoDeLog(TipoDeLog.Passeio, "Walk", walk),
    OpcaoDeLog(TipoDeLog.Comida, "Food", food),
    OpcaoDeLog(TipoDeLog.Agua, "Water", water),
    OpcaoDeLog(TipoDeLog.Medicamento, "Medicine", med),
    OpcaoDeLog(TipoDeLog.Sintoma, "Symptom", symptom),
)

@Composable
private fun MenuDeLog(onEscolha: (TipoDeLog) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        opcoesDeLog.chunked(2).forEach { linha ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                linha.forEach { opcao ->

                    BotaoDeOpcao(
                        opcao = opcao,
                        modifier = Modifier.weight(1f),
                        onClick = { onEscolha(opcao.tipo) }
                    )

                    if (linha.size == 1) Spacer(Modifier.weight(1f))
                }
            }

        }
    }
}

@Composable
private fun BotaoDeOpcao(
    opcao: OpcaoDeLog,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .border(1.dp, outline, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(opcao.cor, CircleShape)
        )
        Text(
            text = opcao.etiqueta,
            style = typography.titleSmall,
            color = textStrong
        )
    }
}