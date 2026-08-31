package com.luisamsampaio.jiggie.features.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.coco
import com.luisamsampaio.jiggie.ui.theme.cocoContainer
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.xixi
import com.luisamsampaio.jiggie.ui.theme.xixiContainer

/**
 * Parte visual do ecrã Passeio.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun PasseioScreenContent(
    state: PasseioUiState,
    onHora: (Int) -> Unit,
    onXixi: () -> Unit,
    onCoco: () -> Unit,
    onMenos: () -> Unit,
    onMais: () -> Unit,
    onGravar: () -> Unit
) {
    Column {
        ChipsDeHora(state.minutosAtras, onHora)

        Spacer(Modifier.height(16.dp))

        Etiqueta("OUTPUT")
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            AlternadorGrande(
                texto = "Pee",
                activo = state.xixi,
                cor = xixi,
                corDeFundo = xixiContainer,
                modifier = Modifier.weight(1f),
                onClick = onXixi
            )
            AlternadorGrande(
                texto = "Poop",
                activo = state.coco,
                cor = coco,
                corDeFundo = cocoContainer,
                modifier = Modifier.weight(1f),
                onClick = onCoco
            )
        }
        Spacer(Modifier.height(16.dp))

        Etiqueta("DURATION")
        Spacer(Modifier.height(8.dp))
        Contador(state.duracao, "min", onMenos, onMais)

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.error, style = typography.bodySmall, color = danger)
        }

        Spacer(Modifier.height(20.dp))

        BotaoPrincipal(
            texto = if (state.isLoading) "Saving…" else "Save entry",
            activo = !state.isLoading,
            onClique = onGravar
        )
    }
}

/**
 * Liga o [PasseioViewModel] ao [PasseioScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun PasseioScreen(
    viewModel: PasseioViewModel = viewModel { PasseioViewModel() },
    caoId: String,
    onGravado: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.gravado) {
        if (state.gravado) {
            onGravado()
            viewModel.reiniciar()
        }
    }

    PasseioScreenContent(
        state = state,
        onHora = viewModel::onHora,
        onXixi = viewModel::onXixi,
        onCoco = viewModel::onCoco,
        onMenos = viewModel::onMenos,
        onMais = viewModel::onMais,
        onGravar = { viewModel.gravar(caoId) }
    )
}
