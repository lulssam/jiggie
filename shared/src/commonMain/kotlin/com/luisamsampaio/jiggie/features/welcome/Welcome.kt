package com.luisamsampaio.jiggie.features.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.BotaoSecundario
import com.luisamsampaio.jiggie.ui.Logotipo
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primaryLink
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import io.github.jan.supabase.realtime.Column

/**
 * Parte visual do ecrã Welcome.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 */
@Composable
private fun WelcomeScreenContent(
    onCreateFamily: () -> Unit,
    onJoinCode: () -> Unit ,
    onLogIn: () -> Unit ,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 420.dp)
                .padding(start = 26.dp, end = 26.dp, bottom = 34.dp)
        ){
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Logotipo(tamanho = 78.dp)

                Spacer(Modifier.height(22.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Jiggie")
                        withStyle(SpanStyle(color = primary)) {
                            append("!")
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = primaryDark
                )

                Spacer(Modifier.height(11.dp))

                Text(
                    text = "Shared health tracking for your whole pack. One family, every dog, on the same page.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 21.sp,
                    color = textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 252.dp)
                )
            }

            BotaoPrincipal(
                texto = "Create a family",
                activo = true,
                onClique = onCreateFamily,
            )

            Spacer(Modifier.height(10.dp))

            BotaoSecundario(
                texto = "Join with a code",
                onClique = onJoinCode
            )

            Spacer(Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textTertiary
                )
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.labelMedium,
                    color = primaryLink,
                    modifier = Modifier.clickable(onClick = onLogIn)
                )


            }
        }
    }

}

/**
 * Como é so um ecrã simples, não é preciso de ViewModel,
 * apenas redireciona para os ecrãs necessários
 */
@Composable
fun WelcomeScreen(
    onCreateFamily: () -> Unit,
    onJoinCode: () -> Unit,
    onLogIn: () -> Unit
) {
    WelcomeScreenContent(
        onCreateFamily = onCreateFamily,
        onJoinCode = onJoinCode,
        onLogIn = onLogIn
    )
}