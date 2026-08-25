package com.luisamsampaio.jiggie

/**
 * Aceita qualquer coisa com um @ pelo meio e um ponto depois.
 *
 * Não é uma verificação a serio, só evita mandar coisas a toa para o servidor.
 * Quem verifica mesmo o email é o supabase.*/
private val EMAIL_PLAUSIVEL = Regex(".+@.+\\..+")

/**
 * Verdeiro quando [email] tem forma de email*/
fun emailPlausivel(email: String): Boolean =
    EMAIL_PLAUSIVEL.matches(email.trim())

const val MINIMO_PASSWORD = 6