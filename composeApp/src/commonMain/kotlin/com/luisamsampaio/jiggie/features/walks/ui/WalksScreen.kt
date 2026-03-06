package com.luisamsampaio.jiggie.features.walks.ui

import androidx.compose.runtime.Composable
import com.luisamsampaio.jiggie.data.repository.Atividade
import com.luisamsampaio.jiggie.features.auth.UserProfile

@Composable
fun WalksScreenContent(
    user: UserProfile,
    onLogout: () -> Unit,
    atividades: List<Atividade>,
) {
}