package com.luisamsampaio.jiggie.features.home

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
    medicamentos: List<MedicamentoDto>,
    sintomas: List<SintomaDto>,
    administracoes: List<AdministracaoDto>
): DadosDoDia {
    // medicamentos: o que estava previsto - o que já foi dado
    val previstas = medicamentos.flatMap { m -> m.hora.map { m.id to minutosDaHora(it) } }
    val dadas = administracoes
        .mapNotNull { a -> a.horaPrevista?.let { a.medicamentoId to minutosDaHora(it) } }
        .toSet()
    val porDar = previstas.filterNot { it in dadas }
    val proxima = porDar.minByOrNull { it.second }

    // passeio, comida, agua
    val ultimoPasseio = passeios.maxByOrNull { Instant.parse(it.quando) }
    val ultimaRefeicao = refeicoes.maxByOrNull { Instant.parse(it.quando) }
    val ultimoSintoma = sintomas.maxByOrNull { Instant.parse(it.quando) }

    val estado = EstadoDoDia(
        medicamentos = medicamentos
            .joinToString(", ") { it.nome }
            .ifEmpty { "None scheduled" },
        medPastilha = when {
            previstas.isEmpty() -> Pastilha("NONE", Tom.Neutro)
            proxima != null -> Pastilha("DUE ${hora12(proxima.second)}", Tom.Alerta)
            else -> Pastilha("ALL GIVEN", Tom.Bom)
        },

        // meds
        medContagem = if (previstas.isEmpty()) "Tap to add"
        else "${previstas.size - porDar.size} / ${previstas.size} given",

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
                    TipoDeRegisto.Passeio
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

private fun etiquetasDoPasseio(passeio: PasseioDto?): List<Pastilha> {
    if (passeio == null) return listOf(Pastilha("NONE YET", Tom.Neutro))

    val etiquetas = buildList {
        if (passeio.xixi) add(Pastilha("PEE", Tom.Xixi))
        if (passeio.coco) add(Pastilha("POOP", Tom.Coco))
    }
    return etiquetas.ifEmpty { listOf(Pastilha("NO OUTPUT", Tom.Neutro)) }
}

/** "08:00:00" → 480. */
private fun minutosDaHora(hora: String): Int {
    val partes = hora.split(":")
    return (partes[0].toIntOrNull() ?: 0) * 60 + (partes.getOrNull(1)?.toIntOrNull() ?: 0)
}

/** 480 → "8:00 AM". */
private fun hora12(minutos: Int): String {
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
private fun horaDe(iso: String): String {
    val local = Instant.parse(iso).toLocalDateTime(TimeZone.currentSystemDefault())
    return hora12(local.hour * 60 + local.minute)
}

/** 1.0 → "1", 1.5 → "1.5". A coluna é numeric(4,2) e "1.0 cup" lê-se mal. */
private fun quantidade(q: Double): String =
    if (q == q.toInt().toDouble()) q.toInt().toString() else q.toString()

private fun base(base: String): String = when (base) {
    "seca" -> "Dry"
    "humida" -> "Wet"
    else -> "Mixed"
}

private fun gravidade(nivel: Int): String = when (nivel) {
    1 -> "Mild"
    2 -> "Moderate"
    else -> "Severe"
}