package com.luisamsampaio.jiggie.features.meds.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MedicamentoTest {

    /** Uma segunda-feira. */
    private val inicio = LocalDate(2026, 10, 5)

    private fun medicamento(frequencia: Frequencia) = Medicamento(
        id = "m",
        nome = "Carprofen",
        dose = "75mg",
        horas = horasPara(2),
        frequencia = frequencia,
        inicio = inicio,
    )

    private fun dia(depoisDoInicio: Int) = inicio.plus(depoisDoInicio, DateTimeUnit.DAY)

    @Test
    fun antesDoInicioNuncaToca() {
        assertFalse(medicamento(Frequencia.Diaria).tocaEm(inicio.minus(1, DateTimeUnit.DAY)))
    }

    @Test
    fun diaSimDiaNaoContaAPartirDoInicio() {
        val m = medicamento(Frequencia.DiaSimDiaNao)

        assertTrue(m.tocaEm(dia(0)))
        assertFalse(m.tocaEm(dia(1)))
        assertTrue(m.tocaEm(dia(2)))
        assertEquals(dia(2), m.proximoDiaDepoisDe(dia(0)))
    }

    @Test
    fun diasDaSemanaSoTocaNessesDias() {
        val m = medicamento(Frequencia.DiasDaSemana(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)))

        assertTrue(m.tocaEm(dia(0)))                        // segunda
        assertFalse(m.tocaEm(dia(1)))                       // terça
        assertEquals(dia(2), m.proximoDiaDepoisDe(dia(0)))  // quarta
    }

    @Test
    fun diasDaSemanaSemDiasNaoExiste() {
        assertFailsWith<IllegalArgumentException> { Frequencia.DiasDaSemana(emptySet()) }
    }
}