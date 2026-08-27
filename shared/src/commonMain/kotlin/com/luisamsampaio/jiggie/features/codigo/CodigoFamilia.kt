package com.luisamsampaio.jiggie.features.codigo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.bordaTracejada
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primaryBorder
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primarySurface
import com.luisamsampaio.jiggie.ui.theme.success
import com.luisamsampaio.jiggie.ui.theme.successContainer
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.check
import org.jetbrains.compose.resources.painterResource

/**
 * Parte visual do ecrã CodigoFamilia.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun CodigoFamiliaScreenContent(
    nome: String,
    codigo: String,
    onEntrar: () -> Unit = {}
) {

    val areaDeTransferencia = LocalClipboardManager.current
    var copiado by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 420.dp)
                .padding(start = 26.dp, end = 26.dp, top = 14.dp, bottom = 34.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {

                // check
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(successContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = null,
                        tint = success
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "$nome is ready",
                    style = MaterialTheme.typography.titleLarge,
                    color = textStrong
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Share this code so family members can join and see the same dogs.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = textTertiary
                )

                Spacer(Modifier.height(22.dp))

                CaixaDoCodigo(codigo = codigo)

                Spacer(Modifier.height(12.dp))

                // copiar codigo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .border(1.dp, outline, RoundedCornerShape(11.dp))
                        .clickable {
                            areaDeTransferencia.setText(AnnotatedString(codigo))
                            copiado = true
                        }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (copiado) "Copied" else "Copy code",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (copiado) success else textBody
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "This code expires in 24 hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = textTertiary
                )

            }

            BotaoPrincipal(
                texto = "Enter Jiggie!",
                activo = true,
                onClique = onEntrar
            )
        }
    }

}

@Composable
fun CodigoFamiliaScreen(
    nome: String,
    codigo: String,
    onEntrar: () -> Unit
) {

    CodigoFamiliaScreenContent(
        nome = nome,
        codigo = codigo,
        onEntrar = onEntrar
    )
}

@Composable
private fun CaixaDoCodigo(codigo: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(primarySurface, RoundedCornerShape(14.dp))
            .bordaTracejada(cor = primaryBorder, raio = 14.dp)
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = codigo,
            fontFamily = plexMono(),
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 3.sp,
            color = primaryDark,
        )
    }
}