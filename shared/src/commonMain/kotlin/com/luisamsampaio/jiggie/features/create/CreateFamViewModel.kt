package com.luisamsampaio.jiggie.features.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Gere o estado e a lógica do ecrã CreateFam.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class CreateFamViewModel : ViewModel() {

    private val _state = MutableStateFlow(CreateFamUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<CreateFamUiState> = _state.asStateFlow()


    /**
     * Quando se clica no botão para criar a familia.*/
    fun onCreateFamilia() {
        val currentState = _state.value

        if (currentState.familyName.isBlank() || currentState.yourName.isBlank()) {
            _state.update {
                it.copy(error = "Fill out all fields")
            }
            return
        }

        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        // chamada ao backend
    }

    /**
     * Quando se troca o nome da familia
     * @param newFamName Novo nome da familia*/
    fun onFamilyNameChange(newFamName: String) {
        _state.update {
            it.copy(
                familyName = newFamName,
                error = null
            )
        }
    }

    /**
     * Quando se troca o nome do membro
     * @param newName Novo nome do membro*/
    fun onYourNameChange(newName: String) {
        _state.update {
            it.copy(
                yourName = newName,
                error = null
            )
        }
    }


    /**
     * Quando se clica no botão para entrar na familia com código de convite*/
    fun onJoinCode() {
        _state.update { it.copy(error = null) }
    }


    /**
     * Voltar quando já se tem uma conta e quer fazer login
     */
    fun onLogin() {
        _state.update { it.copy(error = null) }
    }


    private fun gerarCodigo(): String {
        val alfabeto = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val sufixo = (1..4).map { alfabeto.random() }.joinToString("")
        return "JGY-$sufixo"
    }
}

/**
 * Transforma um erro do backend numa frase legível para o utilizador.
 *
 * @param erro O erro devolvido pelo backend.
 * @return Uma mensagem em português para mostrar no ecrã.
 */
private fun mensagem(erro: Any): String = "Erro desconhecido"
