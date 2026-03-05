package com.luisamsampaio.jiggie.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luisamsampaio.jiggie.features.auth.UserProfile
import jiggie.composeapp.generated.resources.Res
import jiggie.composeapp.generated.resources.app_name
import jiggie.composeapp.generated.resources.noRecentAct
import jiggie.composeapp.generated.resources.recentAct
import jiggie.composeapp.generated.resources.sairBtn
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainLayout(
    user: UserProfile,
    onLogout: () -> Unit,
    currentTab: String,
    onTabSelected: (String) -> Unit,
    pageTitle: String,
    pageSubtitle: String,
    actionButtonText: String,
    onActionClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
            .verticalScroll(scrollState)
    ) {
        // cabeçalo (Sair | Jiggie | Perfil)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SAIR
            Text(
                text = stringResource(Res.string.sairBtn),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLogout() }
            )

            // JIGGIE
            Text(
                text = stringResource(Res.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // PERFIL ATUAL
            Text(
                text = user.displayName,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TABS DE NAVEGAÇÃO
        val tabs = listOf("Remédios", "Água", "Passeios", "Saúde", "Gráficos")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab == currentTab
                Text(
                    text = tab,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onTabSelected(tab) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TITULO E SUBTITULO
        Text(
            text = pageTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = pageSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // formulário dinamico (vem do ecrã especifico)
        content()

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onActionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = actionButtonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ATIVIDADE RECENTE
        Text(
            text = stringResource(Res.string.recentAct),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        //  TODO FIRESTORE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.noRecentAct),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}