package com.luisamsampaio.jiggie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.postgrest.from

private fun String.debug(): String =
    if (isEmpty()) "(vazio)"
    else "'" + this + "'  " + length + " chars  [" +
            map { it.code.toString(16) }.joinToString(" ") + "]"

@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            Text(
                text = "BUILD ${BuildInfo.TIME} · ${BuildInfo.SHA}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp)
            )

            val nomeState = rememberTextFieldState()
            var nomeValue by remember { mutableStateOf("") }
            var numero by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var fundo by remember { mutableStateOf("") }

            val campo = Modifier.fillMaxWidth().padding(horizontal = 8.dp)

            var estado by remember { mutableStateOf("a ligar...") }
            LaunchedEffect(Unit) {
                estado = try {
                    val r = supabase.from("passeio")
                        .select()
                        .decodeList<kotlinx.serialization.json.JsonObject>()
                    "OK — \${r.size} linhas (esperado 0: a RLS bloqueia o anon)"
                } catch (e: Exception) {
                    "\"FALHOU — \${e::class.simpleName}: \${e.message}\""
                }
            }

            Text(estado, Modifier.padding(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text("1 · API nova (state)", Modifier.padding(8.dp))
                    TextField(
                        state = nomeState,
                        label = { Text("Nome") },
                        modifier = campo
                    )
                    Text(nomeState.text.toString().debug(), Modifier.padding(8.dp))
                    HorizontalDivider()
                }

                item {
                    Text("2 · API antiga (value)", Modifier.padding(8.dp))
                    TextField(
                        value = nomeValue,
                        onValueChange = { nomeValue = it },
                        label = { Text("Nome") },
                        modifier = campo
                    )
                    Text(nomeValue.debug(), Modifier.padding(8.dp))
                    HorizontalDivider()
                }

                item {
                    Text("3 · Teclado numérico", Modifier.padding(8.dp))
                    TextField(
                        value = numero,
                        onValueChange = { numero = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = campo
                    )
                    Text(numero.debug(), Modifier.padding(8.dp))
                    HorizontalDivider()
                }

                item {
                    Text("4 · Password", Modifier.padding(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Palavra-passe") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = campo
                    )
                    Text("${password.length} chars", Modifier.padding(8.dp))
                    HorizontalDivider()
                }

                items(50) { i ->
                    Text("Linha $i", Modifier.fillMaxWidth().padding(16.dp))
                    HorizontalDivider()
                }

                item {
                    Text("5 · Último campo — o teclado tapa-o?", Modifier.padding(8.dp))
                    TextField(
                        value = fundo,
                        onValueChange = { fundo = it },
                        label = { Text("Fundo") },
                        modifier = campo
                    )
                }
            }
        }
    }
}