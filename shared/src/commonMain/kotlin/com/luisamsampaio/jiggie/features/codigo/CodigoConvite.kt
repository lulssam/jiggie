package com.luisamsampaio.jiggie.features.codigo

import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * O que o `get_codigo_convite`devolve: o codigo atual e quando expira*/
@Serializable
data class CodigoConvite(
    @SerialName("codigo_convite") val codigoConvite: String,
    @SerialName("codigo_expira_em") val codigoExpiraEm: String
)

/**
 * Vai buscar o código de convite da damilia de quem tem a sessão iniciada.
 *
 * Passa sempre pela função e não pela coluna `familia.codigo_convite`:
 * é a função que gera um código novo quando o antigo expirou. Ler a coluna
 * diretamente devolve o que lá estiver guardado, mesmo que já não sirva*/
suspend fun buscarCodigoConvite(): String =
    supabase.postgrest.rpc("get_codigo_convite")
        .decodeSingle<CodigoConvite>()
        .codigoConvite

