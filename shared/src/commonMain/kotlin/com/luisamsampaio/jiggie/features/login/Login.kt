package com.luisamsampaio.jiggie.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.ui.BotaoPrincipal
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.danger
import com.luisamsampaio.jiggie.ui.theme.plexSans
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryBorder
import com.luisamsampaio.jiggie.ui.theme.primaryContainer
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.primaryLink
import com.luisamsampaio.jiggie.ui.theme.surface
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.ic_pets
import org.jetbrains.compose.resources.painterResource

/**
 * A parte visual do ecrã de login.
 *
 * Não sabe nada sobre o resto da aplicação: recebe o que tem de mostrar e
 * avisa quando o utilizador faz alguma coisa. É por isso que se consegue
 * desenhar sozinha, sem servidor e sem ViewModel.
 *
 * O conteúdo pode deslizar. Num telemóvel pequeno, com o teclado aberto,
 * sobra pouca altura — e sem deslizar os campos ficariam por baixo do teclado.
 *
 * @param state Tudo o que há para mostrar: o que está escrito nos campos, se
 *              estamos à espera do servidor, e se há algum erro.
 * @param onEmail Chamado sempre que a pessoa escreve no campo do email.
 * @param onPassword Chamado sempre que a pessoa escreve no campo da palavra-passe.
 * @param onEntrar Chamado quando a pessoa carrega em "Log in".
 * @param onCriarFamilia Chamado quando a pessoa carrega em "Create a family".
 * @param onEsqueciPalavraPasse Chamado quando a pessoa carrega em "Forgot?".
 */
@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onEmail: (String) -> Unit,
    onPassword: (String) -> Unit,
    onEntrar: () -> Unit,
    onCriarFamilia: () -> Unit,
    onEsqueciPalavraPasse: () -> Unit,
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
        ) {
            BoxWithConstraints(Modifier.weight(1f)) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 34.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .background(primaryContainer, RoundedCornerShape(20.dp))
                                .border(1.5.dp, primaryBorder, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_pets),
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = "Welcome back",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            letterSpacing = (-0.7).sp,
                            color = primaryDark,
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("Log in to your Jiggie")
                                withStyle(SpanStyle(color = primary)) { append("!") }
                                append(" account.")
                            },
                            fontSize = 13.sp,
                            color = textTertiary,
                        )
                    }

                    Etiqueta("EMAIL")
                    Spacer(Modifier.height(7.dp))
                    CampoTexto(
                        valor = state.email,
                        onValor = onEmail,
                        placeholder = "you@email.com",
                        tipoDeTeclado = KeyboardType.Email,
                    )

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Etiqueta("PASSWORD")
                        Text(
                            text = "Forgot?",
                            fontFamily = plexSans(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = primaryLink,
                            modifier = Modifier.clickable(onClick = onEsqueciPalavraPasse),
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    CampoTexto(
                        valor = state.password,
                        onValor = onPassword,
                        placeholder = "••••••••",
                        tipoDeTeclado = KeyboardType.Password,
                        esconderTexto = true,
                    )

                    if (state.error != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = state.error,
                            fontFamily = plexSans(),
                            fontSize = 12.sp,
                            color = danger,
                        )
                    }
                }
            }

            BotaoPrincipal(
                texto = if (state.isLoading) "A entrar…" else "Log in",
                activo = state.podeEntrar && !state.isLoading,
                onClique = onEntrar,
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "New here? ",
                    fontFamily = plexSans(),
                    fontSize = 13.sp,
                    color = textTertiary,
                )
                Text(
                    text = "Create a family",
                    fontFamily = plexSans(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = primaryLink,
                    modifier = Modifier.clickable(onClick = onCriarFamilia),
                )
            }
        }
    }
}


/**
 * Liga o [LoginViewModel] à parte visual.
 *
 * Fica a ouvir o estado e volta a desenhar o ecrã de cada vez que ele muda.
 * Não tem lógica nenhuma — é só o fio entre as duas coisas.
 *
 * @param viewModel Quem guarda o estado e trata da autenticação.
 *                  É criado sozinho se ninguém der outro.
 * @param onCriarFamilia O que fazer quando a pessoa quer criar uma família em
 *                       vez de entrar. Quem decide para onde se navega é o
 *                       ecrã de fora, não este.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
    onCriarFamilia: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LoginScreenContent(
        state = state,
        onEmail = viewModel::onEscreverEmail,
        onPassword = viewModel::onEscreverPassword,
        onEntrar = viewModel::login,
        onCriarFamilia = onCriarFamilia,
        onEsqueciPalavraPasse = {},
    )
}