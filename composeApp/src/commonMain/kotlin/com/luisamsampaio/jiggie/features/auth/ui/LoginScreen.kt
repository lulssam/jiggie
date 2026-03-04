package com.luisamsampaio.jiggie.features.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.auth.LoginViewModel
import com.luisamsampaio.jiggie.features.auth.UserProfile

@Composable
fun LoginScreenContent(
    profiles: List<UserProfile>,
    selectedProfile: UserProfile?,
    onProfileSelected: (UserProfile) -> Unit,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Jiggie!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // icone



        Text(
            text = "Selecione o seu Perfil",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        profiles.forEach { profile ->
            val isSelected = profile == selectedProfile

            Button(
                onClick = { onProfileSelected(profile) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleLarge
                )

            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLoginClick,
            enabled = selectedProfile != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continuar", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    MaterialTheme {
        LoginScreenContent(
            profiles = UserProfile.values().toList(),
            selectedProfile = viewModel.selectedProfile.value,
            onProfileSelected = { viewModel.onProfileSelected(it) },
            onLoginClick = { viewModel.onLoginClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        profiles = UserProfile.values().toList(),
        selectedProfile = UserProfile.CATARINA,
        onProfileSelected = {},
        onLoginClick = {}
    )
}
