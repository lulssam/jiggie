package com.luisamsampaio.jiggie.features.historico

import com.luisamsampaio.jiggie.features.home.Registo
import com.luisamsampaio.jiggie.features.home.TipoDeRegisto
import com.luisamsampaio.jiggie.features.home.base
import com.luisamsampaio.jiggie.features.home.gravidade
import com.luisamsampaio.jiggie.features.home.horaDe
import com.luisamsampaio.jiggie.features.home.quantidade
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * O nome de cada tipo na coluna `tipo` da view*/
internal val TipoDeRegisto.naView: String
    get() = when (this) {
        TipoDeRegisto.Passeio -> "passeio"
        TipoDeRegisto.Comida -> "comida"
        TipoDeRegisto.Agua -> "agua"
        TipoDeRegisto.Medicamento -> "medicamento"
        TipoDeRegisto.Sintoma -> "sintoma"

    }

/**
 * Uma linha da view pronta para mostrar, com o mesmo texto da atividade recente do home
 *
 * @return Null para um tipo que não é reconhecido.*/
internal fun RegistoDto.paraRegisto(): Registo? {
    val tipoDeRegisto = TipoDeRegisto.entries.firstOrNull { it.naView == tipo } ?: return null
    val hora = horaDe(quando)
    val d = dados

    return when (tipoDeRegisto) {
        TipoDeRegisto.Passeio ->
            Registo(id, hora, "Walk", "${d.duracao ?: 0} min", tipoDeRegisto)

        TipoDeRegisto.Comida -> {
            val extras = if (d.extras.isEmpty()) "" else " + ${d.extras.joinToString(", ")}"
            Registo(
                id, hora,
                "Food · ${quantidade(d.quantidade ?: 0.0)} cup",
                base(d.base.orEmpty()) + extras,
                tipoDeRegisto
            )
        }
        TipoDeRegisto.Agua ->
            Registo(id, hora, "Water", "${d.quantidade?.toInt() ?: 0} ml", tipoDeRegisto)

        TipoDeRegisto.Medicamento ->
            Registo(id, hora, "Medicine", listOfNotNull(d.nome, d.dose).joinToString(" · "), tipoDeRegisto)

        TipoDeRegisto.Sintoma -> Registo(
            id, hora, d.sintoma.orEmpty(),
            listOfNotNull(d.gravidade?.let { gravidade(it) }, d.descricao?.ifBlank { null })
                .joinToString(" · "),
            tipoDeRegisto
        )
    }
}

internal fun agruparPorDia(
    linhas: List<RegistoDto>,
    hoje: LocalDate,
    fuso: TimeZone
): List<DiaDoHistorico> =
    linhas
        .groupBy { Instant.parse(it.quando).toLocalDateTime(fuso).date }
        .map { (data, doDia) ->
            DiaDoHistorico(
                data = data,
                titulo = tituloDoDia(data, hoje),
                registos = doDia.mapNotNull { it.paraRegisto() },
            )
        }
        .filter { it.registos.isNotEmpty() }


/** "TODAY", "YESTERDAY" ou "TUE · 09/22" — o formato da data no topo da Home. */
private fun tituloDoDia(data: LocalDate, hoje: LocalDate): String = when (data) {
    hoje -> "TODAY"
    hoje.minus(1, DateTimeUnit.DAY) -> "YESTERDAY"
    else -> {
        val mes = data.month.number.toString().padStart(2, '0')
        val dia = data.day.toString().padStart(2, '0')
        "${data.dayOfWeek.name.take(3)} · $mes/$dia"
    }
}