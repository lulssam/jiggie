package com.luisamsampaio.jiggie.features.log.administracao

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primaryLink
import com.luisamsampaio.jiggie.ui.theme.success
import com.luisamsampaio.jiggie.ui.theme.successContainer
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary

/**
 * Parte visual do ecrã AdministracaoMeds.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun AdministracaoMedsScreenContent(
    state: AdministracaoMedsUiState,
    onDar: (DoseDoDia) -> Unit,
    onGerir: () -> Unit
) {
    Column {
        when {
            state.isLoading -> Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                CircularProgressIndicator()
            }

            state.doses.isEmpty() -> Text(
                text = "Nothing scheduled yet.",
                style = typography.bodyMedium,
                color = textTertiary
            )

            else -> state.doses.forEach { dose ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 9.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, outline, RoundedCornerShape(12.dp))
                        .padding(13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dose.nome,
                            style = typography.titleSmall,
                            color = textStrong
                        )

                        Text(
                            text = dose.horaTexto + if (dose.dada) " · given" else " · scheduled",
                            fontSize = 11.sp,
                            color = textTertiary
                        )
                    }

                    Text(
                        text = if (dose.dada) "Given" else "Give now",
                        style = typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (dose.dada) success else Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (dose.dada) successContainer else primaryDark)
                            .clickable(enabled = !dose.dada) { onDar(dose) }
                            .padding(horizontal = 13.dp, vertical = 9.dp)
                    )
                }
            }
        }

        Text(
            text = "Manage schedule →",
            style = typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = primaryLink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onGerir)
                .padding(vertical = 10.dp)
        )
    }
}

/**
 * Liga o [AdministracaoMedsViewModel] ao [AdministracaoMedsScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun AdministracaoMedsScreen(
    viewModel: AdministracaoMedsViewModel = viewModel { AdministracaoMedsViewModel() },
    caoId: String,
    onDose: () -> Unit,
    onGerir: () -> Unit

) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(caoId) {
        viewModel.carregar(caoId)
    }

    AdministracaoMedsScreenContent(
        state = state,
        onDar = { viewModel.dar(caoId, it) },
        onGerir = onGerir
    )
}