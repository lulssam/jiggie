package com.luisamsampaio.jiggie.features.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // estado vai ser o perfil selecionado
    var selectedProfile = mutableStateOf<UserProfile?>(null)
        private set

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
            // Exemplo: guardar userId nas SharedPreferences/DataStore
            // ou fazer um login anónimo no Firebase e associar este ID.
            println("A iniciar sessão como ${user.displayName} (Role: ${user.role})")
        }
    }

}