package com.luisamsampaio.jiggie.features.log.sintoma

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.luisamsampaio.jiggie.features.log.ChipGravidade
import com.luisamsampaio.jiggie.features.log.ChipSintomas
import com.luisamsampaio.jiggie.features.log.ChipsDeHora
import com.luisamsampaio.jiggie.features.log.DescricaoField
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.theme.danger
import kotlin.String

/**
 * Parte visual do ecrã Sintoma.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun SintomaScreenContent(
    state: SintomaUiState,
    onHora: (Int) -> Unit,
    onTipo: (String) -> Unit,
    onGravidade: (Int) -> Unit,
    onDescricao: (String) -> Unit,
    onGravar: () -> Unit
) {
    Column {
        // hora
        ChipsDeHora(
            minutosAtras = state.minutosAtras,
            onEscolha = onHora
        )

        Spacer(Modifier.height(16.dp))

        // sintomas
        ChipSintomas(
            tipo = state.tipo,
            onClick = onTipo
        )

        Spacer(Modifier.height(16.dp))

        // gravidade
        ChipGravidade(
            gravidade = state.gravidade,
            onEscolha = onGravidade
        )

        Spacer(Modifier.height(16.dp))

        // descrição
        DescricaoField(
            descricao = state.descricao,
            onClick = onDescricao
        )

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.error, style = MaterialTheme.typography.bodySmall, color = danger)
        }

        Spacer(Modifier.height(20.dp))

        BotaoPrincipal(
            texto = if (state.isLoading) "Flag symptom" else "Flag symptom",
            activo = !state.isLoading,
            onClique = onGravar
        )
    }
}

/**
 * Liga o [SintomaViewModel] ao [SintomaScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun SintomaScreen(
    viewModel: SintomaViewModel = viewModel { SintomaViewModel() },
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

    SintomaScreenContent(
        state = state,
        onHora = viewModel::onHora,
        onTipo = viewModel::onTipo,
        onGravidade = viewModel::onGravidade,
        onDescricao = viewModel::onDescricao,
        onGravar = { viewModel.gravar(caoId = caoId) }
    )
}
