package com.luisamsampaio.jiggie.features.charts.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.charts.ChartsViewModel
import com.luisamsampaio.jiggie.ui.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.ui.theme.AguaBorderColor
import com.luisamsampaio.jiggie.ui.theme.AguaGradientDark
import com.luisamsampaio.jiggie.ui.theme.AguaGradientLight
import com.luisamsampaio.jiggie.ui.theme.AguaLabelColor
import com.luisamsampaio.jiggie.ui.theme.AguaValueColor
import com.luisamsampaio.jiggie.ui.theme.CardColor
import com.luisamsampaio.jiggie.ui.theme.ForegroundColor
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.PasseiosBorderColor
import com.luisamsampaio.jiggie.ui.theme.PasseiosGradientDark
import com.luisamsampaio.jiggie.ui.theme.PasseiosGradientLight
import com.luisamsampaio.jiggie.ui.theme.PasseiosLabelColor
import com.luisamsampaio.jiggie.ui.theme.PasseiosValueColor
import com.luisamsampaio.jiggie.ui.theme.RemediosBorderColor
import com.luisamsampaio.jiggie.ui.theme.RemediosGradientDark
import com.luisamsampaio.jiggie.ui.theme.RemediosGradientLight
import com.luisamsampaio.jiggie.ui.theme.RemediosLabelColor
import com.luisamsampaio.jiggie.ui.theme.RemediosValueColor
import com.luisamsampaio.jiggie.ui.theme.SaudeBorderColor
import com.luisamsampaio.jiggie.ui.theme.SaudeGradientDark
import com.luisamsampaio.jiggie.ui.theme.SaudeGradientLight
import com.luisamsampaio.jiggie.ui.theme.SaudeLabelColor
import com.luisamsampaio.jiggie.ui.theme.SaudeValueColor
import com.luisamsampaio.jiggie.ui.theme.SelectorBgColor
import com.luisamsampaio.jiggie.ui.theme.SelectorBorderColor
import com.mmk.kmpauth.google.GoogleUser
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.graficoSubtitle
import jiggie.composeapp.generated.resources.graficoTab
import jiggie.composeapp.generated.resources.totalAgua
import jiggie.composeapp.generated.resources.totalPasseios
import jiggie.composeapp.generated.resources.totalRemedios
import jiggie.composeapp.generated.resources.totalSaude
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChartsScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    mesAnoLabel: String,
    onMesAnterior: () -> Unit,
    onMesSeguinte: () -> Unit,
    totalAgua: Int,
    totalRemedios: Int,
    totalPasseios: Int,
    totalSaude: Int,
    onTabSelected: (String) -> Unit
) {

    val scrollState = rememberScrollState()

    MainLayout(
        user = user,
        onLogout = onLogout,
        currentTab = "Gráficos",
        onTabSelected = onTabSelected,
        pageTitle = stringResource(Res.string.graficoTab),
        pageSubtitle = stringResource(Res.string.graficoSubtitle),
        actionButtonText = "",
        onActionClick = {},
        atividades = emptyList()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // selecionar mes
            Row(
                modifier = Modifier
                    .background(SelectorBgColor, RoundedCornerShape(4.dp))
                    .border(1.5.dp, SelectorBorderColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // botão mes anterior
                Text(
                    text = "◀",
                    fontSize = 14.sp,
                    color = ForegroundColor,
                    modifier = Modifier.clickable { onMesAnterior() }
                )

                // label do mes
                Text(
                    text = mesAnoLabel,
                    color = ForegroundColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                // botão mês seguinte
                Text(
                    text = "▶",
                    fontSize = 14.sp,
                    color = ForegroundColor,
                    modifier = Modifier.clickable { onMesSeguinte() }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // grid 2x2 para cards de estatisticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // água
                StatCard(
                    value = "$totalAgua L",
                    label = stringResource(Res.string.totalAgua),
                    borderColor = AguaBorderColor,
                    valueColor = AguaValueColor,
                    labelColor = AguaLabelColor,
                    gradientLight = AguaGradientLight,
                    gradientDark = AguaGradientDark,
                    modifier = Modifier.weight(1f)

                )
                // remedios
                StatCard(
                    value = "$totalRemedios",
                    label = stringResource(Res.string.totalRemedios),
                    borderColor = RemediosBorderColor,
                    valueColor = RemediosValueColor,
                    labelColor = RemediosLabelColor,
                    gradientLight = RemediosGradientLight,
                    gradientDark = RemediosGradientDark,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // passeios
                StatCard(
                    value = "$totalPasseios",
                    label = stringResource(Res.string.totalPasseios),
                    borderColor = PasseiosBorderColor,
                    valueColor = PasseiosValueColor,
                    labelColor = PasseiosLabelColor,
                    gradientLight = PasseiosGradientLight,
                    gradientDark = PasseiosGradientDark,
                    modifier = Modifier.weight(1f)
                )
                // saude
                StatCard(
                    value = "$totalSaude",
                    label = stringResource(Res.string.totalSaude),
                    borderColor = SaudeBorderColor,
                    valueColor = SaudeValueColor,
                    labelColor = SaudeLabelColor,
                    gradientLight = SaudeGradientLight,
                    gradientDark = SaudeGradientDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    borderColor: Color,
    valueColor: Color,
    labelColor: Color,
    gradientLight: Color,
    gradientDark: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = CardColor,
        border = BorderStroke(1.5.dp, borderColor),
        shadowElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            gradientLight,
                            gradientDark
                        )
                    )
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center

        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // valor grande
                Text(
                    text = value,
                    color = valueColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                // label
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun ChartsScreen(
    user: UserProfile,
    onLogout: () -> Unit,
    viewModel: ChartsViewModel,
    onTabSelected: (String) -> Unit
) {
    ChartsScreenContent(
        user = user,
        onLogout = onLogout,
        mesAnoLabel = viewModel.mesAnoLabel,
        onMesAnterior = { viewModel.mesAnterior() },
        onMesSeguinte = { viewModel.mesSeguinte() },
        totalAgua = viewModel.totalAgua,
        totalRemedios = viewModel.totalRemedios,
        totalPasseios = viewModel.totalPasseios,
        totalSaude = viewModel.totalSaude,
        onTabSelected = onTabSelected
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChartsScreenPreview() {
    JiggieTheme {
        ChartsScreenContent(
            user = UserProfile("1", "Catarina", "USER"),
            onLogout = {},
            mesAnoLabel = "Março 2026",
            onMesAnterior = {},
            onMesSeguinte = {},
            totalAgua = 10,
            totalRemedios = 10,
            totalPasseios = 10,
            totalSaude = 10,
            onTabSelected = {}
        )
    }
}