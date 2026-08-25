package com.luisamsampaio.jiggie.features.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.MINIMO_PASSWORD
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textSecondary
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.left_arrow
import org.jetbrains.compose.resources.painterResource

/**
 * Parte visual do ecrã JoinFamily.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 */
@Composable
private fun JoinFamilyScreenContent(
    state: JoinFamilyUiState,
    onBack: () -> Unit = {},
    onInviteCodeChange: (String) -> Unit = {},
    onYourNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onJoinFamily: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .safeDrawingPadding(),
        contentAlignment = TopCenter
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
                    .verticalScroll(rememberScrollState())
            ) {

                // back button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.offset(x = (-10).dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.left_arrow),
                        contentDescription = "Voltar",
                        tint = textStrong,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))


                // join a family
                Text(
                    text = "Join a family",
                    style = typography.titleLarge,
                    color = textStrong
                )

                Spacer(Modifier.height(6.dp))

                // subtitle
                Text(
                    text = "Enter the code a family member shared with you.",
                    style = typography.bodyMedium,
                    color = textSecondary
                )

                Spacer(Modifier.height(26.dp))

                // forms
                JoinFamForms(
                    state = state,
                    onInviteCodeChange = onInviteCodeChange,
                    onYourNameChange = onYourNameChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange
                )

                if (state.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.error,
                        style = typography.bodySmall,
                        color = danger
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Used to sign in on your other devices.",
                    style = typography.bodySmall,
                    color = textTertiary
                )

            }

            BotaoPrincipal(
                texto = if (state.isLoading) "Joining…" else "Join",
                activo = state.podeJuntar && !state.isLoading,
                onClique = onJoinFamily
            )
        }
    }
}

/**
 * Liga o [JoinFamilyViewModel] ao [JoinFamilyScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 */
@Composable
fun JoinFamilyScreen(
    viewModel: JoinFamilyViewModel = viewModel { JoinFamilyViewModel() },
    onBack: () -> Unit,
    onEntrou: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(
        state.entrou
    ) {
        if (state.entrou) {
            onEntrou()
        }
    }

    JoinFamilyScreenContent(
        state = state,
        onBack = onBack,
        onInviteCodeChange = viewModel::onCodigoChange,
        onYourNameChange = viewModel::onYourNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onJoinFamily = viewModel::onJuntar
    )
}

@Composable
private fun JoinFamForms(
    state: JoinFamilyUiState,
    onInviteCodeChange: (String) -> Unit,
    onYourNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // code
        Etiqueta("INVITE CODE")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.codigo,
            onValor = onInviteCodeChange,
            placeholder = "XXXXXX",
            tipoDeTeclado = KeyboardType.Text,
            estilo = typography.bodyLarge.copy(
                fontFamily = plexMono(),
                fontSize = 16.sp,
                letterSpacing = 2.sp,
            )
        )

        Spacer(Modifier.height(16.dp))

        // name
        Etiqueta("YOUR NAME")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.yourName,
            onValor = onYourNameChange,
            placeholder = "e.g. Matilde",
            tipoDeTeclado = KeyboardType.Text
        )

        Spacer(Modifier.height(16.dp))

        // email
        Etiqueta("EMAIL")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.email,
            onValor = onEmailChange,
            placeholder = "your@email.address",
            tipoDeTeclado = KeyboardType.Email
        )

        Spacer(Modifier.height(16.dp))

        // password
        Etiqueta("PASSWORD")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.password,
            onValor = onPasswordChange,
            placeholder = "At least $MINIMO_PASSWORD characters",
            tipoDeTeclado = KeyboardType.Password,
            esconderTexto = true
        )
    }

}