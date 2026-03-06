package com.luisamsampaio.jiggie.features.medication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisamsampaio.jiggie.data.repository.Atividade
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.ui.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.features.medication.MedicationViewModel
import com.luisamsampaio.jiggie.ui.theme.DestructiveColor
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.Orange500
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.dosagem
import jiggie.composeapp.generated.resources.hora
import jiggie.composeapp.generated.resources.nomeRemedio
import jiggie.composeapp.generated.resources.pill
import jiggie.composeapp.generated.resources.placeholderDosagem
import jiggie.composeapp.generated.resources.placeholderHoraAgua
import jiggie.composeapp.generated.resources.placeholderNomeRemedio
import jiggie.composeapp.generated.resources.regRem
import jiggie.composeapp.generated.resources.remSubtitle
import jiggie.composeapp.generated.resources.remediosTab
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun MedicationScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    atividades: List<Atividade>,
    nomeRemedio: String,
    onNomeRemedioChange: (String) -> Unit,
    dosagem: String,
    onDosagemChange: (String) -> Unit,
    hora: String,
    onHoraChange: (String) -> Unit,
    isSaving: Boolean,
    mensagemSucesso: String?,
    mensagemErro: String?,
    onRegistarClick: () -> Unit,
    limparMensagens: () -> Unit,
    onTabSelected: (String) -> Unit
) {

    MainLayout(
        user = user,
        onLogout = onLogout,
        currentTab = stringResource(Res.string.remediosTab),
        onTabSelected = onTabSelected,
        pageTitle = stringResource(Res.string.regRem),
        pageSubtitle = stringResource(Res.string.remSubtitle),
        actionButtonText = stringResource(Res.string.regRem),
        onActionClick = {
            onRegistarClick()
        },
        titleIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Orange500,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.pill),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        atividades = atividades

    ) {
        // form dos medicamentos
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // campo nome do remédio lado a lado com a dosagem
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nomeRemedio,
                    onValueChange = onNomeRemedioChange,
                    label = { Text(stringResource(Res.string.nomeRemedio)) },
                    placeholder = { Text(stringResource(Res.string.placeholderNomeRemedio)) },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = dosagem,
                    onValueChange = onDosagemChange,
                    label = { Text(stringResource(Res.string.dosagem)) },
                    placeholder = { Text(stringResource(Res.string.placeholderDosagem)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isSaving
                )
            }


            // hora
            OutlinedTextField(
                value = hora,
                onValueChange = onHoraChange,
                label = { Text(stringResource(Res.string.hora)) },
                placeholder = { Text(stringResource(Res.string.placeholderHoraAgua)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving
            )


            // mensagens de feedback
            mensagemSucesso?.let { mensagem ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensagem,
                    color = Color(0xFF16A34A),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                LaunchedEffect(mensagem) {
                    delay(3000)
                    limparMensagens()
                }
            }

            mensagemErro?.let { mensagem ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensagem,
                    color = DestructiveColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                LaunchedEffect(mensagem) {
                    delay(3000)
                    limparMensagens()
                }
            }
        }
    }
}

@Composable
fun MedicationScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    viewModel: MedicationViewModel,
    onTabSelected: (String) -> Unit,
) {
    val atividades by viewModel.atividades.collectAsState()

    MedicationScreenContent(
        user = user,
        onLogout = onLogout,
        atividades = atividades,
        nomeRemedio = viewModel.nomeRemedio,
        onNomeRemedioChange = { viewModel.onNomeRemedioChange(it) },
        dosagem = viewModel.dosagem,
        onDosagemChange = { viewModel.onDosagemChange(it) },
        hora = viewModel.hora,
        onHoraChange = { viewModel.onHoraChange(it) },
        isSaving = viewModel.isSaving,
        mensagemSucesso = viewModel.mensagemSucesso,
        mensagemErro = viewModel.mensagemErro,
        onRegistarClick = {
            viewModel.registarRemedio(
                perfilId = user.id,
                perfilNome = user.displayName
            )
        },
        limparMensagens = { viewModel.limparMensagens() },
        onTabSelected = onTabSelected
    )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MedicationScreenPreview() {
    JiggieTheme {
        val mockAtividades = listOf(
            Atividade(
                id = "1",
                perfilNome = "Luísa",
                acao = "Deu Ben-u-ron",
                categoria = "REMEDIOS"
            ),
            Atividade(
                id = "2",
                perfilNome = "Catarina",
                acao = "Deu Ben-u-ron",
                categoria = "REMEDIOS"
            )
        )
        MedicationScreenContent(
            user = UserProfile("1", "Catarina", "USER"),
            onLogout = {},
            atividades = mockAtividades,
            nomeRemedio = "Ben-u-ron",
            onNomeRemedioChange = {},
            dosagem = "500g",
            onDosagemChange = {},
            hora = "9:30",
            onHoraChange = {},
            isSaving = false,
            mensagemSucesso = null,
            mensagemErro = null,
            onRegistarClick = {},
            limparMensagens = {},
            onTabSelected = {}
        )
    }
}
