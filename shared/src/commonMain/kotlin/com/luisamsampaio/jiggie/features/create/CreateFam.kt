package com.luisamsampaio.jiggie.features.create

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.MINIMO_PASSWORD
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.plexSans
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import com.luisamsampaio.jiggie.ui.theme.textSecondary
import com.luisamsampaio.jiggie.ui.theme.textStrong
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.left_arrow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.paragraph.Alignment

/**
 * Parte visual do ecrã CreateFam.
 *
 * Não sabe nada sobre a lógica da aplicação — apenas mostra o que recebe
 * e avisa quando o utilizador faz algo. Fácil de testar e de pré-visualizar.
 *
 * @param state Tudo o que o ecrã precisa para se mostrar corretamente.
 * @param onBack Ação de voltar.
 * @param onFamilyNameChange Ação de mudar o nome da família.
 * @param onYourNameChange Ação de mudar o nome do utilizador.
 * @param onEmailChange Ação de mudar o email.
 * @param onPasswordChange Ação de mudar a palavra-passe.
 * @param onContinue Ação de continuar: mudar de página se tudo estiver ok.
 */
@Composable
private fun CreateFamScreenContent(
    state: CreateFamUiState,
    onBack: () -> Unit,
    onFamilyNameChange: (String) -> Unit,
    onYourNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .safeDrawingPadding(),
        contentAlignment = TopCenter,
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
                Spacer(Modifier.height(18.dp))

                // create your family
                Text(
                    text = "Create your family",
                    style = typography.titleLarge,
                    color = textStrong,
                )

                Spacer(Modifier.height(6.dp))

                // subtitle
                Text(
                    text = "We'll create your account and give you a code to invite the rest of the household.",
                    style = typography.bodyMedium,
                    color = textSecondary,
                )

                Spacer(Modifier.height(26.dp))

                // forms
                CreateFamForm(
                    state = state,
                    onFamilyNameChange = onFamilyNameChange,
                    onYourNameChange = onYourNameChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange
                )

                if (state.error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.error,
                        style = typography.bodySmall,
                        color = danger,
                    )
                }
            }
            BotaoPrincipal(
                texto = if (state.isLoading) "Creating…" else "Continue",
                activo = state.podeContinuar && !state.isLoading,
                onClique = onContinue
            )
        }
    }
}

/**
 * Liga o [CreateFamViewModel] ao [CreateFamScreenContent].
 *
 * Observa o estado do ViewModel e passa-o para o ecrã.
 * Não contém lógica de UI — apenas faz a ligação.
 *
 * @param viewModel O ViewModel que gere o estado deste ecrã.
 *                  É criado automaticamente pelo Compose se não for fornecido.
 * @param onVoltar O que fazer quando o utilizador clica no botão de voltar.
 * @param onContinue Enviar forms quando se clica no botão continuar
 */
@Composable
fun CreateFamScreen(
    viewModel: CreateFamViewModel = viewModel { CreateFamViewModel() },
    onVoltar: () -> Unit = {},
    onContinue: (String, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsState()

    // quando o código deixa de ser null, a família ficou criada e é altura de sair
    // do ecrã. quem navega é o ecrã, não o viewmodel

    LaunchedEffect(state.codigoCriado) {
        val codigo = state.codigoCriado ?: return@LaunchedEffect
        onContinue(state.familyName, codigo)
    }

    CreateFamScreenContent(
        state = state,
        onBack = onVoltar,
        onFamilyNameChange = viewModel::onFamilyNameChange,
        onYourNameChange = viewModel::onYourNameChange,
        onContinue = viewModel::onCreateFamilia,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange
    )
}


@Composable
private fun CreateFamForm(
    state: CreateFamUiState,
    onFamilyNameChange: (String) -> Unit,
    onYourNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Etiqueta("FAMILY NAME")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.familyName,
            onValor = onFamilyNameChange,
            placeholder = "The Sampaio household",
            tipoDeTeclado = KeyboardType.Text
        )

        Spacer(Modifier.height(16.dp))

        Etiqueta("YOUR NAME")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.yourName,
            onValor = onYourNameChange,
            placeholder = "e.g. Luísa",
            tipoDeTeclado = KeyboardType.Text
        )

        Spacer(Modifier.height(16.dp))

        Etiqueta("EMAIL")
        Spacer(Modifier.height(7.dp))
        CampoTexto(
            valor = state.email,
            onValor = onEmailChange,
            placeholder = "your@email.address",
            tipoDeTeclado = KeyboardType.Email
        )

        Spacer(Modifier.height(16.dp))
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