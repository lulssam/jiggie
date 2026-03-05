package com.luisamsampaio.jiggie.features.dashboard.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.data.repository.Atividade
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.ui.theme.BackgroundColor
import com.luisamsampaio.jiggie.ui.theme.CardColor
import com.luisamsampaio.jiggie.ui.theme.ForegroundColor
import com.luisamsampaio.jiggie.ui.theme.MutedForegroundColor
import com.luisamsampaio.jiggie.ui.theme.Orange500
import com.luisamsampaio.jiggie.ui.theme.Orange600
import com.luisamsampaio.jiggie.ui.theme.PrimaryColor
import com.luisamsampaio.jiggie.ui.theme.SecondaryColor
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.app_name
import jiggie.composeapp.generated.resources.dogicon
import jiggie.composeapp.generated.resources.logout
import jiggie.composeapp.generated.resources.noRecentAct
import jiggie.composeapp.generated.resources.recentAct
import jiggie.composeapp.generated.resources.sairBtn
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainLayout(
    user: UserProfile,
    onLogout: () -> Unit,
    currentTab: String,
    onTabSelected: (String) -> Unit,
    pageTitle: String,
    pageSubtitle: String,
    actionButtonText: String,
    onActionClick: () -> Unit,
    titleIcon: (@Composable () -> Unit)? = null,
    atividades: List<Atividade> = emptyList(),
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    // card cabeçalho superior
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // cabeçalo (Sair | Jiggie | Perfil)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardColor,
            border = BorderStroke(3.dp, PrimaryColor.copy(alpha = 0.2f)),
            shadowElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // logo
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(elevation = 8.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Orange500, Orange600
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.dogicon),
                            contentDescription = "Logo",
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    // nome da app + perfil atual
                    Column(modifier = Modifier.weight(1f)) {
                        // JIGGIE
                        Text(
                            text = stringResource(Res.string.app_name),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Orange500, Orange600
                                    )
                                )
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        // PERFIL ATUAL
                        Text(
                            text = user.displayName,
                            color = PrimaryColor.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // SAIR
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.5.dp, SecondaryColor),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = BackgroundColor),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.logout),
                        contentDescription = "Logo Sair"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(Res.string.sairBtn),
                        color = ForegroundColor,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TABS DE NAVEGAÇÃO
        val tabs = listOf("Remédios", "Água", "Passeios", "Saúde", "Gráficos")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color(0xFFFFD6A8), RoundedCornerShape(10.dp))
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab == currentTab

                    Box(
                        modifier = Modifier
                            .then(
                                if (isSelected) {
                                    Modifier.background(
                                        Color(0xFFFF6900),
                                        RoundedCornerShape(10.dp),
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else ForegroundColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        // formulário dinamico (vem do ecrã especifico)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardColor,
            border = BorderStroke(3.dp, Color(0xFFFFD6A8)),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    titleIcon?.invoke()

                    // TITULO E SUBTITULO
                    Text(
                        text = pageTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7E2A0C),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = pageSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                content()

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Text(
                        text = actionButtonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CardColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ATIVIDADE RECENTE
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = CardColor,
            border = BorderStroke(3.dp, Color(0xFFFEE685)),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.recentAct),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B3306),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (atividades.isEmpty()) {
                    // sem atividades
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.noRecentAct),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFBB4D00),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // lista de atividades recentes
                    atividades.forEach { atividade ->
                        AtividadeItem(atividade)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun AtividadeItem(atividade: Atividade) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = BackgroundColor,
        border = BorderStroke(1.dp, Color(0xFFFFD6A8))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // icon da categoria
            Text(
                text = when (atividade.categoria) {
                    "REMEDIOS" -> "💊"
                    "AGUA" -> "💧"
                    "PASSEIOS" -> "🏃"
                    "SAUDE" -> "🏥"
                    else -> "📋"
                },
                fontSize = 24.sp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = atividade.acao,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ForegroundColor
                )
                Text(
                    text = "por ${atividade.perfilNome}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedForegroundColor
                )
            }
        }
    }
}