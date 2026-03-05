package com.luisamsampaio.jiggie.features.auth.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.auth.LoginViewModel
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.ui.theme.Chart1
import com.luisamsampaio.jiggie.ui.theme.Chart2
import com.luisamsampaio.jiggie.ui.theme.InputBackgroundColor
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.luisamsampaio.jiggie.ui.theme.MutedColor
import com.luisamsampaio.jiggie.ui.theme.MutedForegroundColor
import com.luisamsampaio.jiggie.ui.theme.Orange500
import com.luisamsampaio.jiggie.ui.theme.Orange600
import com.luisamsampaio.jiggie.ui.theme.PrimaryColor
import com.luisamsampaio.jiggie.ui.theme.SecondaryColor
import jiggie.composeapp.generated.resources.AddPerfil
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.app_name
import jiggie.composeapp.generated.resources.continuarBtn
import jiggie.composeapp.generated.resources.dogicon
import jiggie.composeapp.generated.resources.nEncontrou
import jiggie.composeapp.generated.resources.selectPerfil
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.graphics.Shadow

@Composable
fun LoginScreenContent(
    profiles: List<UserProfile>,
    selectedProfile: UserProfile?,
    onProfileSelected: (UserProfile) -> Unit,
    onLoginClick: () -> Unit,
) {
    // background color (ocupa o ecrã todo)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // box que se adapta ao tamanho do card
        Box {
            // card principal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(3.dp, PrimaryColor.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Orange500,
                                    Orange600
                                )
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // icone principal
                    Box(
                        modifier = Modifier
                            .rotate(5F)
                            .size(80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Orange500,
                                        Orange600
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.dogicon),
                            contentDescription = "Logo",
                            Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtítulo
                    Text(
                        text = stringResource(Res.string.selectPerfil),
                        color = MutedForegroundColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lista de Perfis
                    profiles.forEach { profile ->
                        val isSelected = profile == selectedProfile
                        val itemGradient = when (profile.displayName) {
                            "Catarina" -> listOf(Color(0xFFF54900), Color(0xFFBB4d00))
                            "Zé" -> listOf(Color(0xFFFFA726), Color(0xFFFB8C00))
                            "Lu" -> listOf(Color(0xFFFF7043), Color(0xFFF4511E))
                            "Pi" -> listOf(Color(0xFFFF6F00), Color(0xFFE65100))
                            "Rosa" -> listOf(Orange500, Orange600)
                            else -> listOf(Chart1, Chart2)
                        }
                        ProfileListItem(
                            name = profile.displayName,
                            isSelected = isSelected,
                            gradientColors = itemGradient,
                            onClick = { onProfileSelected(profile) },
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão Continuar
                    Button(
                        onClick = onLoginClick,
                        enabled = selectedProfile != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.continuarBtn),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rodapé
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.nEncontrou),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(Res.string.AddPerfil),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                // TODO: Ação para adicionar perfil
                            }
                        )
                    }
                }
            }
            // drop shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 6.dp, y = 8.dp)
                    .background(
                        SecondaryColor.copy(0.5f),
                        RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}

@Composable
fun ProfileListItem(
    name: String,
    isSelected: Boolean,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    // animaççao cor de fundo
    val cardBgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface,
    )

    // animação cor
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Orange500 else Color(0xFFFFD6A8)
    )

    // animação border radius
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
    )

    // animação drop shadow
    val shadowElevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = cardBgColor,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        shadowElevation = shadowElevation,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone do cão no item
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.linearGradient(colors = gradientColors),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.dogicon), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nome
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

//            // Visto (Check) TODO
//            if (isSelected) {
//                Icon(
//                    imageVector = Icons.Filled.CheckCircle,
//                    contentDescription = "Selecionado",
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(28.dp)
//                )
//            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {

    val selected = viewModel.selectedProfile.value

    LoginScreenContent(
        profiles = UserProfile.values().toList(),
        selectedProfile = selected,
        onProfileSelected = { viewModel.onProfileSelected(it) },
        onLoginClick = { viewModel.onLoginClick() }
    )

}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun LoginScreenPreview() {
    JiggieTheme {
        LoginScreenContent(
            profiles = UserProfile.values().toList(),
            selectedProfile = UserProfile.CATARINA,
            onProfileSelected = {},
            onLoginClick = {}
        )
    }
}
