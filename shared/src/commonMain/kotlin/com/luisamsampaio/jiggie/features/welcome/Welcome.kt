package com.luisamsampaio.jiggie.features.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Parte visual do ecrã Welcome.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun WelcomeScreenContent(
    state: WelcomeUiState,
    onCreateFamily: () -> Unit,
    onJoinCode: () -> Unit ,
    onLogIn: () -> Unit ,
) {

}

/**
 * Liga o [WelcomeViewModel] ao [WelcomeScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    WelcomeScreenContent(
        state = state,
        onCreateFamily = {}, // aquela coisa viewmodel::state.onCreateFamily
        onJoinCode = {},
        onLogIn = {}
    )
}