package com.luisamsampaio.jiggie.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // estado vai ser o perfil selecionado
    val selectedProfile = mutableStateOf<UserProfile?>(null)

    /**
     * Função que vai atribuir o perfil selecionado ao estado.
     * @param profile Perfil selecionado
     */
    fun onProfileSelected(profile: UserProfile) {
        selectedProfile.value = profile

    }

    fun onLoginClick() {
        val user = selectedProfile.value
        if (user != null) {
            // TODO: Integrar firestore mais tarde.
            println("A iniciar sessão como ${user.displayName} (Role: ${user.role})")
        }
    }

}