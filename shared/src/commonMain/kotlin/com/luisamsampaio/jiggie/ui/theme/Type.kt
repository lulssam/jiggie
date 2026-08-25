package com.luisamsampaio.jiggie.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import jiggie.shared.generated.resources.Res
import jiggie.shared.generated.resources.ibm_plex_mono_bold
import jiggie.shared.generated.resources.ibm_plex_mono_regular
import jiggie.shared.generated.resources.ibm_plex_mono_semi_bold
import jiggie.shared.generated.resources.ibm_plex_sans_bold
import jiggie.shared.generated.resources.ibm_plex_sans_regular
import jiggie.shared.generated.resources.ibm_plex_sans_semi_bold
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp

/**
 * A letra normal do Jiggie, usada em quase todo o texto da aplicação.
 *
 * @return A família IBM Plex Sans, nos três pesos que temos.
 */

@Composable
fun plexSans() = FontFamily(
    Font(Res.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(Res.font.ibm_plex_sans_semi_bold, FontWeight.SemiBold),
    Font(Res.font.ibm_plex_sans_bold, FontWeight.Bold),
)

/**
 * A letra de largura fixa, onde todos os caracteres ocupam o mesmo espaço.
 *
 * Usa-se onde os caracteres têm de alinhar em coluna ou ser lidos um a um:
 * o código de convite da família, as horas dos medicamentos, e os títulos
 * pequenos em maiúsculas que separam as secções.
 *
 * @return A família IBM Plex Mono, nos três pesos que temos.
 */
@Composable
fun plexMono() = FontFamily(
    Font(Res.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(Res.font.ibm_plex_mono_semi_bold, FontWeight.SemiBold),
    Font(Res.font.ibm_plex_mono_bold, FontWeight.Bold),
)

/**
 * Todos os tamanhos e pesos de letra da aplicação, num sítio só.
 *
 * Depois de isto estar entregue ao tema, cada `Text` já nasce com a letra
 * certa — basta dizer qual dos estilos se quer, e nunca mais se escreve
 * a família nem o tamanho à mão.
 *
 * Os valores vêm do desenho: `headlineSmall` é o título dos ecrãs de entrada,
 * `labelSmall` são as etiquetas em maiúsculas por cima dos campos, e por aí.
 *
 * @return A tabela de estilos pronta a dar ao [JiggieTheme].
 */
@Composable
fun jiggieTypography(): Typography {
    val sans = plexSans()
    val base = Typography()

    // O remember evita reconstruir os quinze estilos de cada vez que
    // qualquer coisa no ecrã muda. Só se refazem se a família mudar.
    return remember(sans) {
        Typography(
            displayLarge = base.displayLarge.copy(fontFamily = sans),
            displayMedium = base.displayMedium.copy(fontFamily = sans),
            displaySmall = base.displaySmall.copy(fontFamily = sans),

            headlineLarge = base.headlineLarge.copy(fontFamily = sans),
            headlineMedium = base.headlineMedium.copy(
                fontFamily = sans, fontSize = 30.sp,
                fontWeight = FontWeight.Bold, letterSpacing = (-1).sp,
            ),
            headlineSmall = base.headlineSmall.copy(
                fontFamily = sans, fontSize = 26.sp,
                fontWeight = FontWeight.Bold, letterSpacing = (-0.7).sp,
            ),

            titleLarge = base.titleLarge.copy(
                fontFamily = sans,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp
            ),
            titleMedium = base.titleMedium.copy(
                fontFamily = sans, fontSize = 19.sp,
                fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp,
            ),
            titleSmall = base.titleSmall.copy(
                fontFamily = sans, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            ),

            bodyLarge = base.bodyLarge.copy(fontFamily = sans, fontSize = 14.sp),
            bodyMedium = base.bodyMedium.copy(fontFamily = sans, fontSize = 13.sp),
            bodySmall = base.bodySmall.copy(fontFamily = sans, fontSize = 12.sp),

            labelLarge = base.labelLarge.copy(
                fontFamily = sans, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            ),
            labelMedium = base.labelMedium.copy(
                fontFamily = sans, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            ),
            labelSmall = base.labelSmall.copy(
                fontFamily = sans, fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp,
            ),
        )
    }
}