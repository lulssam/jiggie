package com.luisamsampaio.jiggie

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

val supabase = createSupabaseClient(
    supabaseUrl = "https://plipzcglegzoypghapdn.supabase.co",
    supabaseKey = "sb_publishable_SMKjxy3fXXlmWq-QHxa-cw_-wWI6Sp-"
) {
    install(Postgrest)
    install(Auth)
    install(Realtime)
}