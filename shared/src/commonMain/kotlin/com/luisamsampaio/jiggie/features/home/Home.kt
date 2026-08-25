package com.luisamsampaio.jiggie.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary

/**
 * Parte visual do ecrã Home.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun HomeScreenContent(
    state: HomeUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .safeDrawingPadding()
            .padding(26.dp)
    ) {
        when {
            state.isLoading ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            state.error != null ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error, color = danger)
                }

            else -> {
                Text(
                    text = state.nome, style = typography.titleLarge, color = textStrong
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = state.familia?.let { "Família: $it" } ?: "Ainda sem familia",
                    style = typography.bodyMedium,
                    color = textTertiary
                )
            }
        }
    }
}

/**
 * Liga o [HomeViewModel] ao [HomeScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() }
) {
    val state by viewModel.state.collectAsState()
    HomeScreenContent(
        state = state,
    )
}

/**
 * Pré-visualização do ecrã Home para usar durante o desenvolvimento.
 *
 * Usa dados fictícios para simular como o ecrã ficará com informação real.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomePreview() {
    // TODO: envolver no tema do projeto
    HomeScreenContent(
        state = HomeUiState(),
    )
}