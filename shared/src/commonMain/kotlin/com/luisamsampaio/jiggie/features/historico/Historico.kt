package com.luisamsampaio.jiggie.features.historico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.surface

/**
 * Parte visual do ecrã Historico.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun HistoricoScreenContent(
    state: HistoricoUiState,
) {
    when {
        state.isLoading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        state.error != null ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = MaterialTheme.colorScheme.error)
            }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(surface)
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 18.dp, top = 4.dp)
            ){

                // titulo da página
                Text(
                    text = "History",
                    style = typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NOME DO CÃO",
                        style = typography.bodySmall
                    )

                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    }
}

/**
 * Liga o [HistoricoViewModel] ao [HistoricoScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun HistoricoScreen(
    viewModel: HistoricoViewModel = viewModel { HistoricoViewModel() }
) {
    val state by viewModel.state.collectAsState()
    HistoricoScreenContent(
        state = state,
    )
}

/**
 * Pré-visualização do ecrã Historico para usar durante o desenvolvimento.
 *
 * Usa dados fictícios para simular como o ecrã ficará com informação real.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoricoPreview() {
    // TODO: envolver no tema do projeto
    HistoricoScreenContent(
        state = HistoricoUiState(),
    )
}