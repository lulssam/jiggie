package com.luisamsampaio.jiggie.features.walks.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.data.repository.Atividade
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.medication.ui.MedicationScreenContent
import com.luisamsampaio.jiggie.features.walks.WalksViewModel
import com.luisamsampaio.jiggie.ui.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.ui.theme.DestructiveColor
import com.luisamsampaio.jiggie.ui.theme.Green500
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.coco
import jiggie.composeapp.generated.resources.duracaoPasseio
import jiggie.composeapp.generated.resources.hora
import jiggie.composeapp.generated.resources.navigation
import jiggie.composeapp.generated.resources.passeioSubtitle
import jiggie.composeapp.generated.resources.passeiosTab
import jiggie.composeapp.generated.resources.placeholderDuracaoPasseio
import jiggie.composeapp.generated.resources.regPasseio
import jiggie.composeapp.generated.resources.tituloWC
import jiggie.composeapp.generated.resources.xixi
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun WalksScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    atividades: List<Atividade>,
    hora: String,
    onHoraChange: (String) -> Unit,

    duracao: Int?,
    onDuracaoChange: (Int) -> Unit,

    xixi: Boolean,
    onXixiChange: (Boolean) -> Unit,

    coco: Boolean,
    onCocoChange: (Boolean) -> Unit,

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
        currentTab = stringResource(Res.string.passeiosTab),
        onTabSelected = onTabSelected,
        pageTitle = stringResource(Res.string.regPasseio),
        pageSubtitle = stringResource(Res.string.passeioSubtitle),
        actionButtonText = stringResource(Res.string.regPasseio),
        onActionClick = {
            onRegistarClick()
        },
        titleIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Green500,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.navigation),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        atividades = atividades
    ) {
        // form dos passeios
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // campo hora
            OutlinedTextField(
                value = hora,
                onValueChange = onHoraChange,
                label = { Text(stringResource(Res.string.hora)) },
                placeholder = { Text(stringResource(Res.string.hora)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving
            )

            // campo duração
            OutlinedTextField(
                value = if (duracao == 0) "" else duracao.toString(),
                onValueChange = { input ->
                    val numero = input.toIntOrNull() ?: 0
                    onDuracaoChange(numero)
                },
                label = { Text(stringResource(Res.string.duracaoPasseio)) },
                placeholder = { Text(stringResource(Res.string.placeholderDuracaoPasseio)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isSaving
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // titulo
                Text(
                    text = stringResource(Res.string.tituloWC),
                    color = Color(0xFF0D542B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // row xixi
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0FDF4))
                        .border(1.5.dp, Color(0xFFB9F8CF), RoundedCornerShape(4.dp))
                        .clickable(enabled = !isSaving) { onXixiChange(!xixi) }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (xixi) Color(0xFF0D542B) else Color(0xFFFFF5E6),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(0.5.dp, Color(0x33D2691E), RoundedCornerShape(4.dp))
                    ) {
                        if (xixi) {
                            Text(text = "✓", color = Color(0xFFFFFFFF), fontSize = 10.sp)
                        }
                    }

                    // texto xixi
                    Text(
                        text = stringResource(Res.string.xixi),
                        color = Color(0xFF0D542B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // row coco
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0FDF4))
                        .border(1.5.dp, Color(0xFFB9F8CF), RoundedCornerShape(4.dp))
                        .clickable(enabled = !isSaving) { onCocoChange(!coco) }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (coco) Color(0xFF0D542B) else Color(0xFFFFF5E6),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(0.5.dp, Color(0x33D2691E), RoundedCornerShape(4.dp))
                    ) {
                        if (coco) {
                            Text(text = "✓", color = Color(0xFFFFFFFF), fontSize = 10.sp)
                        }
                    }

                    // texto xixi
                    Text(
                        text = stringResource(Res.string.coco),
                        color = Color(0xFF0D542B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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
fun WalksScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    viewModel: WalksViewModel,
    onTabSelected: (String) -> Unit,
) {

    val atividades by viewModel.atividades.collectAsState()

    WalksScreenContent(
        user = user,
        onLogout = onLogout,
        atividades = atividades,
        hora = viewModel.hora,
        onHoraChange = { viewModel.onHoraChange(it) },
        duracao = viewModel.duracao,
        onDuracaoChange = { viewModel.onDuracaoChange(it) },
        xixi = viewModel.xixi,
        onXixiChange = { viewModel.onXixiChange(it) },
        coco = viewModel.coco,
        onCocoChange = { viewModel.onCocoChange(it) },
        isSaving = viewModel.isSaving,
        mensagemSucesso = viewModel.mensagemSucesso,
        mensagemErro = viewModel.mensagemErro,
        onRegistarClick = {
            viewModel.registarPasseio(
                perfilId = user.id,
                perfilNome = user.displayName
            )
        },
        limparMensagens = {viewModel.limparMensagens()},
        onTabSelected = onTabSelected
    )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WalksScreenPreview() {
    JiggieTheme {
        val mockAtividade = listOf(
            Atividade(
                id = "7",
                perfilNome = "Luísa",
                acao = "Passeio de 30 min, fez xixi e coco",
                categoria = "PASSEIOS"
            ),

            Atividade(
                id = "8",
                perfilNome = "Catarina",
                acao = "Passeio de 30 min, fez xixi",
                categoria = "PASSEIOS"
            )
        )

        WalksScreenContent(
            user = UserProfile("1", "Catarina", "USER"),
            onLogout = {},
            atividades = mockAtividade,
            hora = "10:45",
            onHoraChange = {},
            duracao = 30,
            onDuracaoChange = {},
            xixi = false,
            onXixiChange = {},
            coco = false,
            onCocoChange = {},
            isSaving = false,
            mensagemSucesso = null,
            mensagemErro = null,
            onRegistarClick = {},
            limparMensagens = {},
            onTabSelected = {}
        )
    }
}