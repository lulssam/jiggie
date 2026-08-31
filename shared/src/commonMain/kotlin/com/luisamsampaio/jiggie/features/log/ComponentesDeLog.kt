package com.luisamsampaio.jiggie.features.log

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.ui.Etiqueta
import com.luisamsampaio.jiggie.ui.theme.inputBorder
import com.luisamsampaio.jiggie.ui.theme.outline
import com.luisamsampaio.jiggie.ui.theme.outlineStrong
import com.luisamsampaio.jiggie.ui.theme.plexMono
import com.luisamsampaio.jiggie.ui.theme.primaryDark
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
                        .clickable {onEscolha(minutos)}
                        .padding(horizontal = 11.dp, vertical = 7.dp)
                )
            }
        }
    }
}


@Composable
fun Contador(
    valor: Int,
    unidade: String,
    onMenos: () -> Unit,
    onMais: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        BotaoDePasso("—", onMenos)

        Text(
            text = "$valor $unidade",
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