package com.luisamsampaio.jiggie.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.ui.theme.inputBorder
import com.luisamsampaio.jiggie.ui.theme.onSurface
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.primary
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.textDisabled
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primaryBorder
import com.luisamsampaio.jiggie.ui.theme.primaryContainer
import com.luisamsampaio.jiggie.ui.theme.primarySurface
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textTertiary
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.ic_pets
import org.jetbrains.compose.resources.painterResource


/**
 * Um campo onde o utilizador escreve texto, com a aparência do Jiggie:
 * uma caixa de cantos redondos, borda fina, e uma sugestão em cinzento
 * enquanto ainda não há nada escrito.
 *
 * Não sabe nada sobre o ecrã onde está a ser usado. Serve tanto para o email
 * do login como para o nome de um cão — é por isso que vive aqui fora e não
 * dentro da pasta do login.
 *
 * @param valor O texto que está neste momento dentro do campo.
 * @param onValor Chamado sempre que o utilizador escreve ou apaga uma letra.
 *                Recebe o texto completo, já com a alteração feita.
 * @param placeholder O texto cinzento que aparece enquanto o campo está vazio,
 *                 para a pessoa perceber o que se espera que escreva ali.
 * @param tipoDeTeclado Que teclado o telemóvel deve abrir. O de email traz a
 *                      arroba à mão; o de palavra-passe desliga as sugestões.
 * @param esconderTexto Verdadeiro para mostrar pontinhos em vez das letras,
 *                      como se espera de uma palavra-passe.
 * @param modifier Espaço e posição que o ecrã de fora quer dar a este campo.
 */
@Composable
fun CampoTexto(
    valor: String,
    onValor: (String) -> Unit,
    placeholder: String,
    tipoDeTeclado: KeyboardType,
    esconderTexto: Boolean = false,
    estilo: TextStyle = MaterialTheme.typography.bodyLarge,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = inputBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 13.dp,
                vertical = 12.dp
            )
    ) {
        if (valor.isEmpty()) {
            Text(
                text = placeholder,
                style = estilo,
                color = textDisabled
            )
        }

        BasicTextField(
            value = valor,
            onValueChange = onValor,
            singleLine = true,
            textStyle = estilo.copy(color = onSurface),
            cursorBrush = SolidColor(primary),
            keyboardOptions = KeyboardOptions(keyboardType = tipoDeTeclado),
            visualTransformation =
                if (esconderTexto) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * O botão grande e escuro que aparece no fundo dos ecrãs de entrada.
 *
 * Quando está inactivo fica cinzento e deixa de responder ao toque, para a
 * pessoa perceber que ainda falta alguma coisa antes de poder avançar.
 *
 * @param texto A palavra que aparece dentro do botão, por exemplo "Log in".
 * @param activo Verdadeiro quando já se pode carregar. Falso pinta-o de
 *               cinzento e ignora os toques.
 * @param onClique Chamado quando o utilizador carrega no botão estando ele activo.
 * @param modifier Espaço e posição que o ecrã de fora quer dar a este botão.
 */
@Composable
fun BotaoPrincipal(
    texto: String,
    activo: Boolean,
    onClique: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(if (activo) primaryDark else outline)
            .clickable(enabled = activo, onClick = onClique)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = if (activo) Color.White else textDisabled,
        )
    }
}

/**
 * O título pequeno em maiúsculas que fica por cima de cada campo,
 * como "EMAIL" ou "FAMILY NAME".
 *
 * @param texto A palavra a mostrar.
 */
@Composable
fun Etiqueta(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = textTertiary
    )
}

@Composable
fun BotaoSecundario(
    texto: String,
    onClique: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .border(1.dp, outline, RoundedCornerShape(13.dp))
            .clickable(onClick = onClique)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.titleSmall,
            color = textBody,
        )
    }
}

/**
 * A caixa arredondada com a patinha, a marca do Jiggie.
 *
 * O raio e o ícone acompanham o tamanho da caixa, para o desenho ser o
 * mesmo nos 66dp do login e nos 78dp das boas-vindas.
 */
@Composable
fun Logotipo(tamanho: Dp, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(tamanho * 0.29f)
    Box(
        modifier = modifier
            .size(tamanho)
            .background(primaryContainer, forma)
            .border(1.5.dp, primaryBorder, forma),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_pets),
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(tamanho * 0.45f),
        )
    }
}

/**
 * Composable da caixa a tracejado.*/
fun Modifier.bordaTracejada(
    cor: Color,
    raio: Dp,
    largura: Dp = 1.5.dp
) = drawBehind {
    val traco = largura.toPx()
    drawRoundRect(
        color = cor,
        topLeft = Offset(traco / 2, traco / 2),
        size = Size(size.width - traco, size.height - traco),
        cornerRadius = CornerRadius(raio.toPx()),
        style = Stroke(
            width = traco,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
        )
    )
}