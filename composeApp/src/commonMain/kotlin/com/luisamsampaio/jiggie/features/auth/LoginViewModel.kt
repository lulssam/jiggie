package com.luisamsampaio.jiggie.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository()

    // estado vai ser o perfil selecionado
    val selectedProfile = mutableStateOf<UserProfile?>(null)

    // lista de perfis que vêm da base de dados (começa vazia)
    val profilesList = mutableStateOf<List<UserProfile>>(emptyList())

    // estado para saber se ainda está a carregar
    val isLoading = mutableStateOf(true)

    init {
        carregarPerfis()
    }

    private fun carregarPerfis() {
        viewModelScope.launch {
            isLoading.value = true
            profilesList.value = userRepository.getPerfis()
            isLoading.value = false
        }
    }

    /**
     * Função que vai atribuir o perfil selecionado ao estado.
     * @param profile Perfil selecionado
     */
    fun onProfileSelected(profile: UserProfile) {
        selectedProfile.value = profile

    }

    fun onLoginClick(onSucess: (UserProfile) -> Unit) {
        val user = selectedProfile.value
        if (user != null) {
            println("A iniciar sessão como ${user.displayName} (Role: ${user.role})")
            onSucess(user)
        }
    }

}