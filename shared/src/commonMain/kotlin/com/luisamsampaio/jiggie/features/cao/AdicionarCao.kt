package com.luisamsampaio.jiggie.features.cao

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.coresDosCaes
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.primaryDark

/**
 * Parte visual do ecrã AdicionarCao.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun AdicionarCaoScreenContent(
    state: AdicionarCaoUiState,
    onNome: (String) -> Unit,
    onRaca: (String) -> Unit,
    onAno: (String) -> Unit,
    onCor: (Int) -> Unit,
    onGravar: () -> Unit
) {
    Column {
        CampoTexto(
            valor = state.nome,
            onValor = onNome,
            placeholder = "Name",
            tipoDeTeclado = KeyboardType.Text
        )

        Spacer(Modifier.height(9.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            CampoTexto(
                valor = state.raca,
                onValor = onRaca,
                placeholder = "Breed",
                tipoDeTeclado = KeyboardType.Text,
                modifier = Modifier.weight(1f),
            )
            CampoTexto(
                valor = state.anoNascimento,
                onValor = onAno,
                placeholder = "Year",
                tipoDeTeclado = KeyboardType.Number,
                modifier = Modifier.width(92.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Etiqueta("COLOR")
        Spacer(Modifier.height(9.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            coresDosCaes.forEachIndexed { indice, cor ->
                val seleccionada = state.cor == indice
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(cor)
                        .border(
                            width = if (seleccionada) 3.dp else 1.dp,
                            color = if (seleccionada) primaryDark else outline,
                            shape = CircleShape,
                        )
                        .clickable { onCor(indice) },
                )
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(state.error, style = MaterialTheme.typography.bodySmall, color = danger)
        }

        Spacer(Modifier.height(20.dp))

        BotaoPrincipal(
            texto = if (state.isLoading) "Adding…" else "Add dog",
            activo = state.podeGravar && !state.isLoading,
            onClique = onGravar,
        )
    }
}

/**
 * Liga o [AdicionarCaoViewModel] ao [AdicionarCaoScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun AdicionarCaoScreen(
    viewModel: AdicionarCaoViewModel = viewModel { AdicionarCaoViewModel() },
    onGravado: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.gravado) {
        if (state.gravado){
            onGravado()
            viewModel.reiniciar()
        }
    }

    AdicionarCaoScreenContent(
        state = state,
        onNome = viewModel::onNome,
        onRaca = viewModel::onRaca,
        onAno = viewModel::onAno,
        onCor = viewModel::onCor,
        onGravar = viewModel::gravar
    )
}