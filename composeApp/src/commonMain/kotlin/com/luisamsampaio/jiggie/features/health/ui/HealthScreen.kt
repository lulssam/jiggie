package com.luisamsampaio.jiggie.features.health.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.data.repository.Atividade
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.health.HealthViewModel
import com.luisamsampaio.jiggie.ui.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.ui.theme.DestructiveColor
import com.luisamsampaio.jiggie.ui.theme.HealthBadgeBorder
import com.luisamsampaio.jiggie.ui.theme.HealthBadgeSelectedBg
import com.luisamsampaio.jiggie.ui.theme.HealthBadgeSelectedText
import com.luisamsampaio.jiggie.ui.theme.HealthBadgeText
import com.luisamsampaio.jiggie.ui.theme.HealthNotasPlaceholder
import com.luisamsampaio.jiggie.ui.theme.HealthTitleColor
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.Orange500
import com.luisamsampaio.jiggie.ui.theme.Red500
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.heart
import jiggie.composeapp.generated.resources.notas
import jiggie.composeapp.generated.resources.pill
import jiggie.composeapp.generated.resources.placeholderNotas
import jiggie.composeapp.generated.resources.regSaude
import jiggie.composeapp.generated.resources.saudeSubtitle
import jiggie.composeapp.generated.resources.saudeTab
import jiggie.composeapp.generated.resources.sintomasPreocupacoes
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.sin

@Composable
fun HealthScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    atividades: List<Atividade>,
    sintomasDisponiveis: List<String>,
    sintomasSelecionados: Set<String>,
    onSintomasChange: (String) -> Unit,
    notas: String,
    onNotasChange: (String) -> Unit,
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
        currentTab = "Saúde",
        onTabSelected = onTabSelected,
        pageTitle = stringResource(Res.string.saudeTab),
        pageSubtitle = stringResource(Res.string.saudeSubtitle),
        actionButtonText = stringResource(Res.string.regSaude),
        onActionClick = {
            onRegistarClick()
        },
        titleIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Red500,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.heart),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        atividades = atividades
    ) {

        // form da saude
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // label sintomas/preocupações
            Text(
                text = stringResource(Res.string.sintomasPreocupacoes),
                color = HealthTitleColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // badges dos sintomas
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                sintomasDisponiveis.forEach { sintoma ->
                    val isSelected = sintomasSelecionados.contains(sintoma)
                    SintomaBadge(
                        text = sintoma,
                        isSelected = isSelected,
                        enabled = !isSaving,
                        onClick = { onSintomasChange(sintoma) }
                    )
                }
            }

            // label notas
            Text(
                text = stringResource(Res.string.notas),
                color = HealthTitleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            OutlinedTextField(
                value = notas,
                onValueChange = onNotasChange,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.placeholderNotas),
                        color = HealthNotasPlaceholder
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                enabled = !isSaving,
                maxLines = 4,
                shape = RoundedCornerShape(4.dp),
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
fun SintomaBadge(
    text: String,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) HealthBadgeSelectedBg else Color.Transparent
    val textColor = if (isSelected) HealthBadgeSelectedText else HealthBadgeText
    val borderColor = if (isSelected) HealthBadgeSelectedBg else HealthBadgeBorder

    Box(
        modifier = Modifier
            .border(1.5.dp, borderColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun HealthScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    viewModel: HealthViewModel,
    onTabSelected: (String) -> Unit
) {
    val atividades by viewModel.atividades.collectAsState()

    HealthScreenContent(
        user = user,
        onLogout = onLogout,
        atividades = atividades,
        sintomasDisponiveis = viewModel.sintomas,
        sintomasSelecionados = viewModel.sintomasSelecionados,
        onSintomasChange = { viewModel.onSintomaChange(it) },
        notas = viewModel.notas,
        onNotasChange = { viewModel.onNotasChange(it) },
        isSaving = viewModel.isSaving,
        mensagemSucesso = viewModel.mensagemSucesso,
        mensagemErro = viewModel.mensagemErro,
        onRegistarClick = {
            viewModel.registarNotaSaude(
                perfilId = user.id,
                perfilNome = user.displayName,
            )
        },
        limparMensagens = { viewModel.limparMensagens() },
        onTabSelected = onTabSelected
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HealthScreenPreview() {
    JiggieTheme {
        val mockAtividade = listOf(
            Atividade(
                id = "9",
                perfilNome = "Luísa",
                acao = "Com Diarreia e Sem Apetite",
                categoria = "SAUDE"
            ),
            Atividade(
                id = "10",
                perfilNome = "Pi",
                acao = "Cochear",
                categoria = "SAUDE"
            )
        )

        HealthScreenContent(
            user = UserProfile("1", "Catarina", "USER"),
            onLogout = {},
            atividades = mockAtividade,
            sintomasDisponiveis = listOf(
                "Vómito",
                "Otite",
                "Cochear",
                "Sem Apetite",
                "Diarreia",
                "Infeção Urinária"
            ),
            sintomasSelecionados = setOf(""),
            onSintomasChange = {},
            notas = "Sem notas",
            onNotasChange = {},
            isSaving = false,
            mensagemSucesso = null,
            mensagemErro = null,
            onRegistarClick = {},
            limparMensagens = {},
            onTabSelected = {}
        )
    }
}