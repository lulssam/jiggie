package com.luisamsampaio.jiggie.features.meds.domain

import kotlinx.datetime.LocalDate

/**
 * De onde vem o hoje.
 *
 * É uma porta, como o repositório: em prod é o relgio do
 * sistema, nos testes é um dia fixo. Sem isto, os testes mudavam
 * de resulado à meia-noite*/
fun interface Calendario {
    fun hoje(): LocalDate
}