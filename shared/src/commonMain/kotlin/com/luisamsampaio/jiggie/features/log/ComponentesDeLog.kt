package com.luisamsampaio.jiggie.features.log

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.ui.CampoTexto
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.foodDark
import com.luisamsampaio.jiggie.ui.theme.inputBorder
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.outlineStrong
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primaryDark
import com.luisamsampaio.jiggie.ui.theme.symptom
import com.luisamsampaio.jiggie.ui.theme.symptomBorder
import com.luisamsampaio.jiggie.ui.theme.symptomDark
import com.luisamsampaio.jiggie.ui.theme.symptomSub
import com.luisamsampaio.jiggie.ui.theme.textBody
import com.luisamsampaio.jiggie.ui.theme.textSecondary
import com.luisamsampaio.jiggie.ui.theme.textStrong

@Composable
fun ChipsDeHora(
    minutosAtras: Int,
    onEscolha: (Int) -> Unit
) {
    val opcoes = listOf(0 to "Now", 30 to "30m ago", 60 to "1h ago", 120 to "2h ago")

    Column {
        Etiqueta("TIME")
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            opcoes.forEach { (minutos, texto) ->
                val ativo = minutos == minutosAtras

                Text(
                    text = texto,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ativo) Color.White else textBody,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (ativo) primaryDark else Color.White)
                        .border(
                            1.dp, if (ativo) primaryDark else inputBorder,
                            RoundedCornerShape(9.dp)
                        )
                        .clickable { onEscolha(minutos) }
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }
    }
}


@Composable
fun Contador(
    texto: String,
    onMenos: () -> Unit,
    onMais: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotaoDePasso("—", onMenos)

        Text(
            text = texto,
            fontFamily = plexMono(),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = textStrong,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 78.dp)
        )

        BotaoDePasso("+", onMais)
    }
}

@Composable
private fun BotaoDePasso(sinal: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, outlineStrong, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(sinal, fontSize = 22.sp, color = textBody)
    }
}

@Composable
fun AlternadorGrande(
    texto: String,
    activo: Boolean,
    cor: Color,
    corDeFundo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(if (activo) corDeFundo else Color.White)
            .border(1.5.dp, if (activo) cor else outline, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = typography.titleSmall,
            color = if (activo) cor else textSecondary
        )
    }
}

/**
 * Pills presets para valores
 * @param texto String do chip
 * @param ativo Booleano que indica se o chip está ativo ou não
 * @param onClick Função a ser executada quando o chip é clicado
 * @param corAtiva Fundo quando selecionado. Leva sempre texto branco por cima
 * @param raio Raio do rounded corner shape
 * */

@Composable
fun ChipEscolha(
    texto: String,
    ativo: Boolean,
    onClick: () -> Unit,
    corAtiva: Color = primaryDark,
    raio: Dp = 10.dp
) {
    Text(
        text = texto,
        style = typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = if (ativo) Color.White else textBody,
        modifier = Modifier
            .clip(RoundedCornerShape(raio))
            .background(if (ativo) corAtiva else Color.White)
            .border(
                width = 1.dp,
                color = if (ativo) corAtiva else inputBorder,
                shape = RoundedCornerShape(raio)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
    )

}

@Composable
fun ChipBase(
    base: String,
    onClick: (String) -> Unit,
) {
    val opcoes = listOf("seca" to "Kibble", "humida" to "Wet", "mista" to "Mixed")

    Column {
        Etiqueta("BASE")
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            opcoes.forEach { (valor, texto) ->
                val ativo = valor == base
                Text(
                    text = texto,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ativo) Color.White else textBody,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (ativo) primaryDark else Color.White)
                        .border(
                            1.dp, if (ativo) primaryDark else inputBorder,
                            RoundedCornerShape(9.dp)
                        )
                        .clickable { onClick(valor) }
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipExtras(
    extras: List<String>,
    onClick: (String) -> Unit
) {
    val opcoes = listOf(
        "Rice", "Chicken", "Olive Oil", "Pumpkin", "Egg", "Yogurt"
    )

    Column {
        Etiqueta("EXTRAS")
        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            opcoes.forEach { extra ->
                ChipEscolha(
                    texto = extra,
                    ativo = extra in extras,
                    onClick = { onClick(extra) },
                    corAtiva = foodDark,
                    raio = 18.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipSintomas(
    tipo: String,
    onClick: (String) -> Unit
) {
    val opcoes = listOf(
        "Limping", "Vomiting", "Diarrhea", "Lethargy", "Itching", "Appetite", "Coughing", "Other"
    )

    Column {
        Etiqueta("WHAT'S OFF?")
        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            opcoes.forEach { sintoma ->
                ChipEscolha(
                    texto = sintoma,
                    ativo = sintoma == tipo,
                    onClick = { onClick(sintoma) },
                    corAtiva = symptomDark,
                    raio = 18.dp
                )
            }
        }
    }
}

@Composable
fun ChipGravidade(
    gravidade: Int,
    onEscolha: (Int) -> Unit
) {
    val opcoes = listOf(1 to "Mild", 2 to "Moderate", 3 to "Severe")

    Column {
        Etiqueta("SEVERITY")
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            opcoes.forEach { (valor, texto) ->
                val ativo = valor == gravidade

                Text(
                    text = texto,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ativo) Color.White else textBody,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (ativo) symptomDark else Color.White)
                        .border(
                            1.dp, if (ativo) symptomDark else inputBorder,
                            RoundedCornerShape(9.dp)
                        )
                        .clickable { onEscolha(valor) }
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
fun DescricaoField(
    descricao: String,
    onClick: (String) -> Unit
){
    Column {
        Etiqueta("NOTE")
        Spacer(Modifier.height(8.dp))

        CampoTexto(
            valor = descricao,
            onValor = onClick,
            placeholder = "Describe what you noticed…",
            tipoDeTeclado = KeyboardType.Text,
            estilo = typography.bodyMedium,
            modifier = Modifier.height(64.dp)
        )
    }
}