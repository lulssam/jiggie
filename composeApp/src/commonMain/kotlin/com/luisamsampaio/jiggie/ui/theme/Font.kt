package com.luisamsampaio.jiggie.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.space_grotesk_bold
import jiggie.composeapp.generated.resources.space_grotesk_light
import jiggie.composeapp.generated.resources.space_grotesk_medium
import jiggie.composeapp.generated.resources.space_grotesk_regular
import jiggie.composeapp.generated.resources.space_grotesk_semi_bold

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


@OptIn(ExperimentalResourceApi::class)
@Composable
fun SpaceGroteskFamily() = FontFamily(
    Font(Res.font.space_grotesk_light, weight = FontWeight.Light),
    Font(Res.font.space_grotesk_regular, weight = FontWeight.Normal),
    Font(Res.font.space_grotesk_medium, weight = FontWeight.Medium),
    Font(Res.font.space_grotesk_semi_bold, weight = FontWeight.SemiBold),
    Font(Res.font.space_grotesk_bold, weight = FontWeight.Bold)
)

@Composable
fun SpaceGroteskTypography() = Typography().run {
    val fontFamily = SpaceGroteskFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}

