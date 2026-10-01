package com.luisamsampaio.jiggie.historico

import com.luisamsampaio.jiggie.features.historico.DadosDoRegisto
import com.luisamsampaio.jiggie.features.historico.RegistoDto
import com.luisamsampaio.jiggie.features.historico.agruparPorDia
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgruparPorDiaTest {

    private val hoje = LocalDate(2026, 9, 25)

    private fun agua(id: String, quando: String) =
        RegistoDto(id, "agua", quando, DadosDoRegisto(quantidade = 250.0))

    @Test
    fun agrupaPorDiaEChamaHojeEOntemPeloNome() {
        val dias = agruparPorDia(
            linhas = listOf(
                agua("a", "2026-09-25T09:00:00Z"),
                agua("b", "2026-09-25T07:30:00Z"),
                agua("c", "2026-09-24T21:00:00Z"),
                agua("d", "2026-09-22T12:00:00Z"),
            ),
            hoje = hoje,
            fuso = TimeZone.UTC,
        )

        assertEquals(listOf("TODAY", "YESTERDAY", "TUE · 09/22"), dias.map { it.titulo })
        assertEquals(listOf(2, 1, 1), dias.map { it.registos.size })
    }

    @Test
    fun tipoDesconhecidoNaoRebentaOEcra() {
        val dias = agruparPorDia(
            linhas = listOf(RegistoDto("x", "peso", "2026-09-25T09:00:00Z", DadosDoRegisto())),
            hoje = hoje,
            fuso = TimeZone.UTC,
        )

        assertTrue(dias.isEmpty())
    }
}