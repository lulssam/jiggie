package com.luisamsampaio.jiggie.features.home

import com.luisamsampaio.jiggie.features.meds.domain.MedicacaoDeHoje
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** Tudo o que o homescreen mostra*/
data class DadosDoDia(
    val estado: EstadoDoDia,
    val recentes: List<Registo>,
    val sinalizado: Sinalizado?
)

fun resumirDia(
    passeios: List<PasseioDto>,
    refeicoes: List<ComidaDto>,
    aguas: List<AguaDto>,
    medicacao: MedicacaoDeHoje,
    sintomas: List<SintomaDto>,
    administracoes: List<AdministracaoDto>
): DadosDoDia {
    // medicamentos: o domínio dos medicamentos já sabe quais tocam hoje,
    // quais estão arquivados e que tomas já foram dadas
    val tomasDeHoje = medicacao.planos.flatMap { it.tomas }
    val porDar = tomasDeHoje.filterNot { it.dada }
    val proxima = porDar.minByOrNull { it.hora }
    val semMedicamentos = medicacao.planos.isEmpty()

    // passeio, comida, agua
    val ultimoPasseio = passeios.maxByOrNull { Instant.parse(it.quando) }
    val ultimaRefeicao = refeicoes.maxByOrNull { Instant.parse(it.quando) }
    val ultimoSintoma = sintomas.maxByOrNull { Instant.parse(it.quando) }

    val estado = EstadoDoDia(
        medicamentos = medicacao.planos
            .joinToString(", ") { it.medicamento.nome }
            .ifEmpty { "None scheduled" },
        medPastilha = when {
            semMedicamentos -> Pastilha("NONE", Tom.Neutro)
            tomasDeHoje.isEmpty() -> Pastilha("NOT TODAY", Tom.Neutro)
            proxima != null -> Pastilha("DUE ${hora12(proxima.hora.hour * 60 + proxima.hora.minute)}", Tom.Alerta)
            else -> Pastilha("ALL GIVEN", Tom.Bom)
        },

        // meds
        medContagem = when {
            semMedicamentos -> "Tap to add"
            tomasDeHoje.isEmpty() -> "Rest day"
            else -> "${tomasDeHoje.size - porDar.size} / ${tomasDeHoje.size} given"
        },

        // passeios
        passeio = ultimoPasseio
            ?.let { "${horaDe(it.quando)} · ${it.duracao} min" }
            ?: "No walk logged",
        passeioEtiquetas = etiquetasDoPasseio(ultimoPasseio),

        // comida
        comida = ultimaRefeicao?.let {
            val extras = if (it.extras.isEmpty()) "" else " + ${it.extras.joinToString(", ")}"
            "${horaDe(it.quando)} · ${quantidade(it.quantidade)} cup$extras"
        } ?: "No meals yet",
        comidaContagem = "${refeicoes.size}/3",

        // agua
        agua = when (aguas.size) {
            0 -> "No water logged"
            1 -> "1 refill today"
            else -> "${aguas.size} refills today"
        },
        aguaTotal = "${aguas.sumOf { it.quantidade }} ml",
    )

    // recent activities
    val recentes = buildList {
        passeios.forEach {
            add(
                it.quando to Registo(
                    it.id,
                    horaDe(it.quando),
                    "Walk",
                    "${it.duracao} min",
                    TipoDeRegisto.Passeio,
                    pastilhasDoPasseio(it.xixi, it.coco)
                )
            )
        }
        refeicoes.forEach {
            val extras = if (it.extras.isEmpty()) "" else " + ${it.extras.joinToString(", ")}"
            add(
                it.quando to Registo(
                    it.id, horaDe(it.quando),
                    "Food · ${quantidade(it.quantidade)} cup",
                    base(it.base) + extras,
                    TipoDeRegisto.Comida
                )
            )
        }

        aguas.forEach {
            add(
                it.quando to Registo(
                    it.id,
                    horaDe(it.quando),
                    "Water",
                    "${it.quantidade} ml",
                    TipoDeRegisto.Agua
                )
            )
        }

        administracoes.forEach {
            val med = it.medicamento
            add(
                it.quando to Registo(
                    it.id, horaDe(it.quando), "Medicine",
                    if (med == null) "" else "${med.nome} · ${med.dose}",
                    TipoDeRegisto.Medicamento
                )
            )
        }

        sintomas.forEach {
            add(
                it.quando to Registo(
                    it.id,
                    horaDe(it.quando), it.tipo,
                    listOfNotNull(gravidade(it.gravidade), it.descricao?.ifBlank { null })
                        .joinToString(" · "),
                    TipoDeRegisto.Sintoma
                )
            )
        }
    }
        .sortedByDescending { Instant.parse(it.first) }
        .take(4)
        .map { it.second }

    val sinalizado = ultimoSintoma?.let {
        Sinalizado(
            titulo = "${gravidade(it.gravidade)} ${it.tipo.lowercase()}",
            subtitulo = listOfNotNull(horaDe(it.quando), it.descricao?.ifBlank { null })
                .joinToString(" · ")
        )
    }

    return DadosDoDia(estado, recentes, sinalizado)
}

/** 480 → "8:00 AM". */
internal fun hora12(minutos: Int): String {
    val h = minutos / 60
    val m = minutos % 60
    val doze = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else -> h
    }
    return "$doze:${m.toString().padStart(2, '0')} ${if (h < 12) "AM" else "PM"}"
}

/** A hora local de um timestamp da base. */
internal fun horaDe(iso: String): String {
    val local = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
    return hora12(local.hour * 60 + local.minute)
}

/** 1.0 → "1", 1.5 → "1.5". A coluna é numeric(4,2) e "1.0 cup" lê-se mal. */
internal fun quantidade(q: Double): String =
    if (q == q.toInt().toDouble()) q.toInt().toString() else q.toString()

/**
 * As bases da comida: o valor da coluna `base` e o texto que se mostra.
 *
 * É a única tradução — o formulário e as listas usam esta, para não
 * voltarem a dizer coisas diferentes.
 */
internal val basesDaComida = listOf(
    "seca" to "Kibble",
    "humida" to "Wet",
    "mista" to "Mixed",
)

internal fun base(base: String): String =
    basesDaComida.firstOrNull { it.first == base }?.second ?: base

internal fun gravidade(nivel: Int): String = when (nivel) {
    1 -> "Mild"
    2 -> "Moderate"
    else -> "Severe"
}