package com.luisamsampaio.jiggie.features.medication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luisamsampaio.jiggie.features.auth.UserProfile
import com.luisamsampaio.jiggie.features.dashboard.ui.MainLayout
import com.luisamsampaio.jiggie.ui.theme.JiggieTheme
import com.mmk.kmpauth.google.GoogleUser
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.dosagem
import jiggie.composeapp.generated.resources.hora
import jiggie.composeapp.generated.resources.nomeRemedio
import jiggie.composeapp.generated.resources.placeholderDosagem
import jiggie.composeapp.generated.resources.placeholderHora
import jiggie.composeapp.generated.resources.placeholderNomeRemedio
import jiggie.composeapp.generated.resources.regRem
import jiggie.composeapp.generated.resources.remSubtitle
import jiggie.composeapp.generated.resources.remediosTab
import org.jetbrains.compose.resources.stringResource
import kotlin.math.sin

@Composable
fun MedicationScreen(
    user: UserProfile,
    onLogout: () -> Unit
) {
    // estados para guardar o que o utilizador escreve nos campos
    var nomeRemedio by remember { mutableStateOf("") }
    var dosagem by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    MainLayout(
        user = user,
        onLogout = onLogout,
        currentTab = stringResource(Res.string.remediosTab),
        onTabSelected = { novaTab ->
            // TODO: navegação entre tabs
            println("Navegar para $novaTab")
        },
        pageTitle = stringResource(Res.string.regRem),
        pageSubtitle = stringResource(Res.string.remSubtitle),
        actionButtonText = stringResource(Res.string.regRem),
        onActionClick = {
            // TODO: Chamar o viewModel para guardar no Firestore
            println("A guardar remédio: $nomeRemedio, $dosagem às $hora")
        }
    ) {
        // form dos medicamentos
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // campo nome do remédio
            OutlinedTextField(
                value = nomeRemedio,
                onValueChange = { nomeRemedio = it },
                label = { Text(stringResource(Res.string.nomeRemedio)) },
                placeholder = { Text(stringResource(Res.string.placeholderNomeRemedio)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // campo dosagem e hora lado a lado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = dosagem,
                    onValueChange = { dosagem = it },
                    label = { Text(stringResource(Res.string.dosagem)) },
                    placeholder = { Text(stringResource(Res.string.placeholderDosagem)) },
                    modifier = Modifier.weight(2f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text(stringResource(Res.string.hora)) },
                    placeholder = { Text(stringResource(Res.string.placeholderHora)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MedicationScreenPreview() {
    JiggieTheme {

        MedicationScreen(
            user = UserProfile("1", "Catarina", "USER"),
            onLogout = {}
        )
    }
}