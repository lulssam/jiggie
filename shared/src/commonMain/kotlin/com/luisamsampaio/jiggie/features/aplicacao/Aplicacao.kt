package com.luisamsampaio.jiggie.features.aplicacao

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.luisamsampaio.jiggie.features.home.HomeScreen
import com.luisamsampaio.jiggie.ui.theme.divider
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import io.github.jan.supabase.realtime.Column
import jiggie.shared.generated.resources.Res
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

    Scaffold(
        containerColor = surface,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            BarraDeBaixo(
                separadores = separadores,
                onLog = { popUp = TipoDeLog.Menu }
            )
        }
    ) { espaco ->
        NavHost(
            navController = separadores,
            startDestination = Home,
            modifier = Modifier.padding(espaco)
        ) {
            composable<Home> { HomeScreen() }
            composable<Historico> { /*TODO: implementar ecrã historico*/ }
            composable<Medicamentos> { /*TODO: implementar ecrã medicamentos*/ }
            composable<Relatorio> { /*TODO: implementar ecrã relatório*/ }
        }
    }

    popUp?.let { tipo ->
        ModalBottomSheet(
            onDismissRequest = { popUp = null },
            containerColor = surface,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
        ) {
            Text(
                text = "PopUp: $tipo",
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun BarraDeBaixo(
    separadores: NavHostController,
    onLog: () -> Unit
) {
    val entrada by separadores.currentBackStackEntryAsState()
    val destino = entrada?.destination

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(surface)
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = divider
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, top = 9.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Separador(
                Res.drawable.home, "Home",
                ativo = destino?.hasRoute<Home>() == true
            ) { separadores.irPara(Home) }

            Separador(
                Res.drawable.history, "History",
                ativo = destino?.hasRoute<Historico>() == true
            ) { separadores.irPara(Historico) }

            BotaoDeLog(onLog)

            Separador(
                Res.drawable.meds, "Meds",
                ativo = destino?.hasRoute<Medicamentos>() == true
            ) { separadores.irPara(Medicamentos) }

            Separador(
                Res.drawable.summarize, "Vet",
                ativo = destino?.hasRoute<Relatorio>() == true
            ) { separadores.irPara(Relatorio) }
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
            contentDescription = null,
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
            fontSize = 9.sp,
            fontWeight = SemiBold,
            letterSpacing = 0.2.sp,
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
