package com.luisamsampaio.jiggie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
// Removemos a importação do GoogleSignInButton que estava a dar erro!

@Composable
fun App() {
    var authReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = "340576670575-ipgas39mbr3uch2l81gp37nehnokgh1g.apps.googleusercontent.com"
                )
            )
            authReady = true
        } catch (e: Exception) {
            println("Erro ao iniciar Google Auth: ${e.message}")
        }
    }

    MaterialTheme {
        if (authReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // A lógica invisível do Google mantém-se
                GoogleButtonUiContainer(
                    onGoogleSignInResult = { googleUser ->
                        val tokenId = googleUser?.idToken
                        println("TOKEN: $tokenId")
                    }
                ) {
                    // AQUI ESTÁ A MAGIA!
                    // Usamos um botão normal do Compose, imune a erros de versão do kmpauth.
                    Button(
                        onClick = { this.onClick() } // O 'this' continua a chamar a janela do Google!
                    ) {
                        Text("Entrar com o Google")
                    }
                }
            }
        }
    }
}