package com.luisamsampaio.jiggie.features.meds.ui

import com.luisamsampaio.jiggie.features.home.hora12
import com.luisamsampaio.jiggie.features.meds.DiaUi
import com.luisamsampaio.jiggie.features.meds.FormularioUi
import com.luisamsampaio.jiggie.features.meds.MedicamentoUi
import com.luisamsampaio.jiggie.features.meds.OpcaoDeFrequencia
import com.luisamsampaio.jiggie.features.meds.TomaUi
import com.luisamsampaio.jiggie.features.meds.domain.Frequencia
import com.luisamsampaio.jiggie.features.meds.domain.MedicacaoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.PlanoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.horasPara
import com.luisamsampaio.jiggie.features.meds.domain.proximoDiaDepoisDe
import com.luisamsampaio.jiggie.features.meds.domain.tocaEm
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.previousOrSame

/** Domínio → ecrã. Os textos estão em inglês, como o resto da app. */
internal fun MedicacaoDeHoje.paraUi(): List<MedicamentoUi> = planos.map { it.paraUi(hoje) }

private fun PlanoDeHoje.paraUi(hoje: LocalDate): MedicamentoUi {
    val m = medicamento
    val segunda = hoje.previousOrSame(DayOfWeek.MONDAY)
    return MedicamentoUi(
        id = m.id,
        nome = m.nome,
        detalhe = "${m.dose} · ${textoDaFrequencia(m)}",
        semana = (0..6).map { i ->
            val dia = segunda.plus(i, DateTimeUnit.DAY)
            DiaUi(letra = "MTWTFSS"[i].toString(), toca = m.tocaEm(dia), hoje = dia == hoje)
        },
        tomas = tomas.map { TomaUi(it.hora, horaEmTexto(it.hora), it.dada) },
        proxima = if (tomas.isNotEmpty()) null else "Not due today · next dose ${textoDoProximo(m, hoje)}",
    )
}

/** "2× daily", "every other day · 2×", "Mon, Wed, Fri" — o schedLabel do design. */
private fun textoDaFrequencia(m: Medicamento): String {
    val vezes = m.horas.size
    val sufixo = if (vezes > 1) " · $vezes×" else ""
    return when (val f = m.frequencia) {
        Frequencia.Diaria -> "$vezes× daily"
        Frequencia.DiaSimDiaNao -> "every other day$sufixo"
        is Frequencia.DiasDaSemana -> textoDosDias(f.dias) + sufixo
    }
}

private fun textoDoProximo(m: Medicamento, hoje: LocalDate): String {
    val proximo = m.proximoDiaDepoisDe(hoje) ?: return "—"
    return if (proximo == hoje.plus(1, DateTimeUnit.DAY)) "tomorrow" else nomeCurto(proximo.dayOfWeek)
}

internal fun textoDosDias(dias: Set<DayOfWeek>): String =
    dias.sorted().joinToString(", ") { nomeCurto(it) }

/** MONDAY → "Mon". */
private fun nomeCurto(dia: DayOfWeek): String =
    dia.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

/** 08:00 → "8:00 AM", com a mesma função que a Home usa. */
internal fun horaEmTexto(hora: LocalTime): String = hora12(hora.hour * 60 + hora.minute)

/** A linha de resumo do formulário: "Every day · at 8:00 AM, 6:00 PM". */
internal fun FormularioUi.resumo(): String {
    val quando = when (opcao) {
        OpcaoDeFrequencia.Diaria -> "Every day"
        OpcaoDeFrequencia.DiaSimDiaNao -> "Every other day, starting today"
        OpcaoDeFrequencia.DiasDaSemana -> if (dias.isEmpty()) "Pick at least one day" else textoDosDias(dias)
    }
    return "$quando · at " + horasPara(vezesPorDia).joinToString(", ") { horaEmTexto(it) }
}