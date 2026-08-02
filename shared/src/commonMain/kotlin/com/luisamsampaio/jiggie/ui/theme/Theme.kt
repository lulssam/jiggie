package com.luisamsampaio.jiggie.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * As cores do Jiggie no formato que os componentes do Material precisam.
 *
 * Serve para que coisas que não desenhamos nós — o indicador de espera, a
 * seleção de texto, o brilho ao tocar num botão — apareçam com as nossas
 * cores em vez das cores roxas que o Material traz de origem.
 */
private val esquemaDeCores = lightColorScheme(
    primary = primary,
    onPrimary = Color.White,
    primaryContainer = primaryContainer,
    onPrimaryContainer = primaryDark,
    background = background,
    onBackground = textStrong,
    surface = surface,
    onSurface = onSurface,
    outline = outline,
    error = danger,
    onError = Color.White,
)

/**
 * Embrulha a aplicação inteira e define o aspecto por omissão de tudo:
 * as cores e a letra.
 *
 * A partir daqui, um `Text` sem mais nada já sai em IBM Plex Sans. Só é
 * preciso dizer qual o estilo — por exemplo `MaterialTheme.typography.bodyMedium`.
 *
 * @param content Todos os ecrãs da aplicação, que passam a herdar este aspecto.
 */
@Composable
fun JiggieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = esquemaDeCores,
        typography = jiggieTypography(),
        content = content,
    )
}