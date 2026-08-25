package com.luisamsampaio.jiggie.features.create

import com.luisamsampaio.jiggie.emailPlausivel

/**
 * Tudo o que o ecrã CreateFam precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 * @property familyName O nome que a pessoa já escreveu para a família.
 * @property yourName O nome com que a pessoa se vai apresentar aos outros membros.
 * @property codigoCriado O código de convite gerado pelo backend. Null enquanto a familia não for
 *                          criada. Quando deixa de ser null, o ecrã sabe que pode navegar.
 * @property isLoading True enquanto estamos à espera de dados do backend.
 *                     O ecrã mostra um indicador de carregamento durante este tempo.
 * @property error Mensagem de erro para mostrar ao utilizador.
 *                 Null significa que não há nenhum erro.
 */
data class CreateFamUiState(
    val familyName: String = "",
    val yourName: String = "",
    val email: String = "",
    val password: String = "",
    val codigoCriado: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * Verdadeiro quando já faz sentido deixar a pessoa carregar em "Continue":
     * os dois nomes estão preenchidos.
     *
     * Fica aqui, e não dentro do ecrã, porque decidir se uns dados servem ou
     * não é uma regra da aplicação. O ecrã limita-se a obedecer ao resultado.
     */
    val podeContinuar: Boolean
        get() = familyName.isNotBlank() &&
                yourName.isNotBlank() &&
                emailPlausivel(email) &&
                password.length >= MINIMO_PASSWORD

    companion object {
        /** O mínimo que o Supabase aceita. Barramos aqui para não gastar uma ida ao servidor. */
        const val MINIMO_PASSWORD = 6
    }
}