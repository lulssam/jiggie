package com.luisamsampaio.jiggie.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // estado vai ser o perfil selecionado
    var selectedProfile by mutableStateOf<UserProfile?>(null)
        private set

    /**
     * Função que vai atribuir o perfil selecionado ao estado.
     * @param profile Perfil selecionado
     */
    fun onProfileSelected(profile: UserProfile) {
        selectedProfile = profile

    }

    fun onLoginClick() {
        val user = selectedProfile
        if (user != null) {
            // TODO: Integrar firestore mais tarde.
            println("A iniciar sessão como ${user.displayName} (Role: ${user.role})")
        }
    }

}