package com.luisamsampaio.jiggie.features.home

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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.StabilityInferred
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.bordaTracejada
import com.luisamsampaio.jiggie.ui.theme.coresDosCaes
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.divider
import com.luisamsampaio.jiggie.ui.theme.dog1
import com.luisamsampaio.jiggie.ui.theme.haloAvatar
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.outlineStrong
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryBorder
import com.luisamsampaio.jiggie.ui.theme.primaryContainer
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primarySurface
import com.luisamsampaio.jiggie.ui.theme.success
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.dog
import jiggie.shared.generated.resources.plus
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

/**
 * Parte visual do ecrã Home.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onAdicionarCao: () -> Unit = {},
    onCao: (String) -> Unit = {}
) {
    when {
        state.isLoading ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        state.error != null ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = danger)
            }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(surface)
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 4.dp)
        ) {
            Cabecalho(state)
            Spacer(Modifier.height(22.dp))

            Text(
                text = "Welcome, ${state.primeiroNome}.",
                style = typography.titleLarge,
                color = primaryDark
            )

            Spacer(Modifier.height(22.dp))

            if (state.temCaes) {
                ChipsCaes(
                    caes = state.caes,
                    idAtivo = state.caoAtivo?.id,
                    onCao = onCao,
                    onAdicionarCao = onAdicionarCao
                )
            } else {
                CartaoAdicionarPrimeiroCao(onAdicionarCao)
                Spacer(Modifier.height(22.dp))
                PassosIniciais()
                Spacer(Modifier.height(22.dp))
                CartaoDeConvite(state.codigoFamilia)
            }
        }
    }
}

/**
 * Liga o [HomeViewModel] ao [HomeScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    versao: Int = 0,
    onAdicionarCao: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(versao) {
        if (versao > 0) viewModel.carregar()
    }

    HomeScreenContent(
        state = state,
        onAdicionarCao = onAdicionarCao,
        onCao = viewModel::onCao
    )
}

@Composable
private fun Cabecalho(state: HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // titulo app
        Text(
            text = buildAnnotatedString {
                append("Jiggie")
                withStyle(SpanStyle(color = primary)) { append("!") }
            },
            style = typography.titleLarge,
            color = primaryDark
        )

        // avatar + dia
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // halo effect
            Box(
                modifier = Modifier
                    .size(31.dp)
                    .background(haloAvatar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.primeiroNome.firstOrNull()?.uppercase().orEmpty(),
                        style = typography.labelSmall,
                        color = Color.White
                    )
                }
            }
            Text(
                text = dataDeHoje(),
                fontFamily = plexMono(),
                fontSize = 11.sp,
                color = textTertiary
            )
        }
    }
}

@Composable
private fun CartaoAdicionarPrimeiroCao(
    onAdicionar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(primarySurface)
            .clickable(onClick = onAdicionar)
            .bordaTracejada(cor = primaryBorder, raio = 16.dp)
            .padding(horizontal = 20.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, primaryBorder.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.dog),
                contentDescription = "Dog",
                tint = primary,
                modifier = Modifier.size(32.dp)
            )

        }
        Spacer(Modifier.height(13.dp))

        Text(
            text = "Add your first dog",
            style = typography.titleMedium,
            color = primaryDark
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Name, breed and age. Takes about 20 seconds.",
            style = typography.bodyMedium,
            color = textTertiary,
            textAlign = TextAlign.Center

        )

        Spacer(Modifier.height(15.dp))

        // botão
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(11.dp))
                .background(primaryDark)
                .padding(horizontal = 22.dp, vertical = 11.dp)
        ) {
            Text(
                text = "+ Add dog",
                style = typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PassosIniciais() {
    val passos = listOf(
        PassoInicial("Add your first dog", "Name, breed, age"),
        PassoInicial("Set up medicine", "Doses and times, so reminders line up"),
        PassoInicial("Log your first walk", "Then the daily status card fills in")
    )

    Column {
        // titulo da secção
        Text(
            text = "GETTING STARTED",
            fontFamily = plexMono(),
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = textTertiary
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .border(1.dp, outline, RoundedCornerShape(13.dp))
        ) {
            passos.forEachIndexed { indice, passo ->
                if (indice > 0) HorizontalDivider(thickness = 1.dp, color = divider)
                LinhaDePasso(numero = indice + 1, passo = passo, actual = indice == 0)
            }
        }
    }
}

@Composable
private fun LinhaDePasso(
    numero: Int,
    passo: PassoInicial,
    actual: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(21.dp)
                .background(if (actual) primaryDark else divider, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // numero
            Text(
                text = numero.toString(),
                fontFamily = plexMono(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (actual) Color.White else textTertiary
            )
        }

        Column(Modifier.weight(1f)) {

            Text(
                text = passo.titulo,
                style = typography.titleSmall,
                color = if (actual) textStrong else textTertiary,
            )
            Text(
                text = passo.subtitulo,
                fontSize = 11.sp,
                color = textDisabled,
            )
        }
    }
}

@Composable
private fun CartaoDeConvite(codigo: String) {

    val areaDeTransferencia = LocalClipboardManager.current
    var copiado by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "INVITE THE HOUSEHOLD",
            fontFamily = plexMono(),
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = textTertiary
        )

        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .border(1.dp, outline, RoundedCornerShape(13.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "Anyone with this code sees the same dogs and can log entries.",
                style = typography.bodySmall,
                color = textTertiary
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(primarySurface, RoundedCornerShape(10.dp))
                        .padding(horizontal = 13.dp, vertical = 11.dp)
                        .widthIn(min = 78.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = codigo,
                        fontFamily = plexMono(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp,
                        letterSpacing = 2.5.sp,
                        color = primaryDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, outline, RoundedCornerShape(10.dp))
                        .clickable {
                            areaDeTransferencia.setText(AnnotatedString(codigo))
                            copiado = true
                        }
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = if (copiado) "Copied" else "Copy",
                        style = typography.labelMedium,
                        color = if (copiado) success else textBody
                    )
                }
            }
        }
    }


}


/**
 * Devolve a data do dia de hoje*/
private fun dataDeHoje(): String {
    val hoje = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dia = hoje.dayOfWeek.name.take(3)
    val mes = hoje.month.number.toString().padStart(2, '0')
    val diaDoMes = hoje.day.toString().padStart(2, '0')

    return "$dia · $mes/$diaDoMes"
}

/**
 * Função para calcular a idade do cão a partir do ano de nascimento*/
private fun idadeEmAnos(
    nascimento: String?
): Int? {
    val ano = nascimento?.take(4)?.toIntOrNull() ?: return null
    return Clock.System.todayIn(TimeZone.currentSystemDefault()).year - ano
}

private data class PassoInicial(val titulo: String, val subtitulo: String)

@Composable
private fun ChipsCaes(
    caes: List<CaoDto>,
    idAtivo: String?,
    onCao: (String) -> Unit,
    onAdicionarCao: () -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        caes.forEach { cao ->
            val ativo = cao.id == idAtivo
            val detalhe = listOfNotNull(
                cao.raca?.trim()?.ifBlank { null },
                idadeEmAnos(cao.nascimento)?.toString()
            ).joinToString(" · ")
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (ativo) primaryContainer else surface)
                    .border(
                        width = if (ativo) 2.dp else 1.dp,
                        color = if (ativo) primary else outline,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onCao(cao.id) }
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(coresDosCaes.getOrElse(cao.cor) { dog1 }, CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = cao.nome.firstOrNull()?.uppercase().orEmpty(),
                        style = typography.titleSmall,
                        color = Color.White
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = cao.nome,
                        style = typography.titleSmall,
                        color = textStrong
                    )

                    if (detalhe.isNotEmpty()) {
                        Text(
                            text = detalhe,
                            fontSize = 12.sp,
                            color = textTertiary
                        )
                    }
                }

            }
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick =  onAdicionarCao)
                .bordaTracejada(cor = outlineStrong, raio = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.plus),
                contentDescription = "Add dog",
                tint = textDisabled,
                modifier = Modifier.size(18.dp)
            )
        }

    }
}
