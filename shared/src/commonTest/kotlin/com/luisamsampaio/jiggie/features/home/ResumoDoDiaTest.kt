package com.luisamsampaio.jiggie.features.home

import com.luisamsampaio.jiggie.features.meds.domain.Frequencia
import com.luisamsampaio.jiggie.features.meds.domain.MedicacaoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.PlanoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.horasPara
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ResumoDoDiaTest {

    @Test
    fun medicamentoQueNaoTocaHojeEDiaDeDescanso() {
        val gabapentin = Medicamento(
            "m", "Gabapentin", "100mg", horasPara(1), Frequencia.DiaSimDiaNao, LocalDate(2026, 10, 4),
        )

        val dia = resumirDia(
            passeios = emptyList(),
            refeicoes = emptyList(),
            aguas = emptyList(),
            medicacao = MedicacaoDeHoje(LocalDate(2026, 10, 5), listOf(PlanoDeHoje(gabapentin, tomas = emptyList()))),
            sintomas = emptyList(),
            administracoes = emptyList(),
        )

        assertEquals("NOT TODAY", dia.estado.medPastilha.texto)
        assertEquals("Rest day", dia.estado.medContagem)
    }
}