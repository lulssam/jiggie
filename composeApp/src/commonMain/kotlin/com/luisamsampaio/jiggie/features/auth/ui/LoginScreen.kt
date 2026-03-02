package com.luisamsampaio.jiggie.features.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luisamsampaio.jiggie.features.auth.LoginViewModel

@Composable
fun LoginScreenContent(
    email: String, // input email
    password: String, // input pass
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEntryClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
}

@Composable
fun LoginScreen(
) {

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        email = "teste@exemplo.com",
        password = "1234",
        onEmailChange = {},
        onPasswordChange = {},
        onEntryClick = {},
        onRegisterClick = {}
    )
}
