package com.luisamsampaio.jiggie

import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException

/**
 * Transforma um erro do Supabase numa frase para mostrar ao utilizador.
 *
 * Os códigos JG00x são os que as funções SQL levantam; os `AuthErrorCode`
 * vêm da autenticação. Ambos são estáveis — ao contrário do texto das
 * mensagens, que muda quando alguém reescreve o SQL.
 */
fun mensagemDeErro(erro: Throwable): String = when (erro) {
    is PostgrestRestException -> when (erro.code) {
        "JG001" -> "Sessão perdida. Entra outra vez."
        "JG002" -> "Já pertences a uma família."
        "JG003" -> "O teu perfil ainda não está pronto. Tenta outra vez."
        "JG004" -> "Esse código não existe. Confirma se o escreveste bem."
        "JG005" -> "Esse código já expirou. Pede um novo a quem to deu."
        else -> "Não foi possível concluir. Tenta outra vez."
    }
    is AuthRestException -> when (erro.errorCode) {
        AuthErrorCode.UserAlreadyExists,
        AuthErrorCode.EmailExists -> "Já existe uma conta com esse email."
        AuthErrorCode.WeakPassword -> "Escolhe uma palavra-passe mais forte."
        AuthErrorCode.EmailAddressInvalid -> "Esse email não parece válido."
        AuthErrorCode.SignupDisabled -> "Os registos estão desactivados de momento."
        AuthErrorCode.InvalidCredentials -> "Email ou palavra-passe errados."
        AuthErrorCode.OverRequestRateLimit -> "Demasiadas tentativas. Espera um pouco."
        else -> "Não foi possível concluir. Tenta outra vez."
    }
    is HttpRequestException -> "Sem ligação. Verifica a internet."
    else -> "Não foi possível concluir. Tenta outra vez."
}