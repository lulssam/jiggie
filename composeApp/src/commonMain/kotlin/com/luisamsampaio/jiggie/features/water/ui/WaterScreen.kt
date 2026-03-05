package com.luisamsampaio.jiggie.features.water.ui

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
import com.luisamsampaio.jiggie.features.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.features.water.WaterViewModel
import com.luisamsampaio.jiggie.ui.theme.Blue500
import com.luisamsampaio.jiggie.ui.theme.DestructiveColor
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.aguaSubtitle
import jiggie.composeapp.generated.resources.aguaTab
import jiggie.composeapp.generated.resources.droplet
import jiggie.composeapp.generated.resources.hora
import jiggie.composeapp.generated.resources.placeholderHoraAgua
import jiggie.composeapp.generated.resources.placeholderQtdAgua
import jiggie.composeapp.generated.resources.placeholderQuantidade
import jiggie.composeapp.generated.resources.qtdAgua
import jiggie.composeapp.generated.resources.regAgua
import jiggie.composeapp.generated.resources.unidadeAgua
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WaterScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    viewModel: WaterViewModel,
    onTabSelected: (String) -> Unit
) {
    // recolher atividades do flow
    val atividades by viewModel.atividades.collectAsState()

    WaterScreenContent(
        user = user,
        onLogout = onLogout,
        atividades = atividades,
        quantidade = viewModel.quantidade,
        onQuantidadeChange = { viewModel.onQuantidadeChange(it) },
        unidade = viewModel.unidade,
        onUnidadeChange = { viewModel.onUnidadeChange(it) },
        hora = viewModel.hora,
        onHoraChange = { viewModel.onHoraChange(it) },
        isSaving = viewModel.isSaving,
        mensagemSucesso = viewModel.mensagemSucesso,
        mensagemErro = viewModel.mensagemErro,
        onRegistarClick = {
            viewModel.registarAgua(
                perfilId = user.id,
                perfilNome = user.displayName
            )
        },
        limparMensagens = { viewModel.limparMensagens() },
        onTabSelected = onTabSelected
    )

}

@Composable
fun WaterScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    atividades: List<Atividade>,
    quantidade: String,
    onQuantidadeChange: (String) -> Unit,
    unidade: String,
    onUnidadeChange: (String) -> Unit,
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
        currentTab = stringResource(Res.string.aguaTab),
        onTabSelected = { novaTab ->
            // TODO: navegação entre tabs
            println("Navegar para $novaTab")
        },
        pageTitle = stringResource(Res.string.regAgua),
        pageSubtitle = stringResource(Res.string.aguaSubtitle),
        actionButtonText = stringResource(Res.string.regAgua),
        onActionClick = onRegistarClick,
        titleIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Blue500, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.droplet),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        atividades = atividades
    ) {
        // form da agua
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // campo da quantidade e unidade lado a lado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = onQuantidadeChange,
                    label = { Text(stringResource(Res.string.qtdAgua)) },
                    placeholder = { Text(stringResource(Res.string.placeholderQtdAgua)) },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    enabled = !isSaving
                )

                OutlinedTextField(
                    value = unidade,
                    onUnidadeChange,
                    label = { Text(stringResource(Res.string.unidadeAgua)) },
                    placeholder = { Text(stringResource(Res.string.placeholderQuantidade)) },
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

            // Mensagens de Sucesso ou Erro
            mensagemSucesso?.let { mensagem ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensagem,
                    color = Color(0xFF16A34A), // Verde
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
                    color = DestructiveColor, // Vermelho do teu tema
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WaterScreenPreview() {
    JiggieTheme {

        val mockUser = UserProfile(id = "1", displayName = "Luísa", role = "ADMIN")

        val mockAtividades = listOf(
            Atividade(
                id = "1",
                perfilNome = "Luísa",
                acao = "Bebeu água (250 ml)",
                categoria = "AGUA"
            ),
            Atividade(
                id = "2",
                perfilNome = "Catarina",
                acao = "Bebeu água (100 ml)",
                categoria = "AGUA"
            )
        )

        WaterScreenContent(
            user = mockUser,
            onLogout = {},
            atividades = mockAtividades,
            quantidade = "250",
            onQuantidadeChange = {},
            unidade = "1L",
            onUnidadeChange = {},
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