package com.luisamsampaio.jiggie.features.meds.data

import com.luisamsampaio.jiggie.features.meds.domain.Frequencia
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentoNovo
import com.luisamsampaio.jiggie.features.meds.domain.horasPara
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class MedicamentoDtoTest {

    @Test
    fun diasIsoViramDiasDaSemana() {
        val dto = MedicamentoDto(
            id = "m", nome = "Apoquel", dose = "5.4mg",
            hora = listOf("18:00:00", "08:00:00"),
            frequencia = "dias_da_semana", inicio = "2026-10-05", dias = listOf(1, 3),
        )

        val m = dto.paraDominio()

        assertEquals(Frequencia.DiasDaSemana(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)), m.frequencia)
        assertEquals(listOf(LocalTime(8, 0), LocalTime(18, 0)), m.horas)
    }

    @Test
    fun medicamentoNovoVaiComDiasIso() {
        val novo = MedicamentoNovo(
            nome = "Apoquel", dose = "5.4mg", horas = horasPara(1),
            frequencia = Frequencia.DiasDaSemana(setOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)),
            inicio = LocalDate(2026, 10, 5),
        )

        val dto = novo.paraDto(caoId = "c")

        assertEquals("dias_da_semana", dto.frequencia)
        assertEquals(listOf(1, 7), dto.dias)
        assertEquals(listOf("09:00"), dto.hora)
    }
}