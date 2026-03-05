package com.luisamsampaio.jiggie.features.water

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import com.luisamsampaio.jiggie.data.repository.Atividade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WaterViewModel : ViewModel() {

    private val activityRepository = ActivityRepository()

    var quantidade by mutableStateOf("")
        private set
    var unidade by mutableStateOf("")
        private set
    var hora by mutableStateOf("")
        private set

    // estado para feedback ao user
    var isSaving by mutableStateOf(false)
        private set
    var mensagemSucesso by mutableStateOf<String?>(null)
        private set
    var mensagemErro by mutableStateOf<String?>(null)
        private set


    // atividades recentes (em tempo real Firestore)
    private val _atividades = MutableStateFlow<List<Atividade>>(emptyList())
    val atividades: MutableStateFlow<List<Atividade>> = _atividades

    init {
        observarAtividades()
    }

    private fun observarAtividades() {
        viewModelScope.launch {
            try {
                activityRepository.getAtividades().collect { atividade ->
                    _atividades.value = atividade.filter { it.categoria == "AGUA" }
                }
            } catch (e: Exception) {
                println("Erro ao observar atividades: ${e.message}")

            }
        }
    }

    /**Setter da quantidade
     * @param novaQuantidade Nova quantidade*/
    fun onQuantidadeChange(novaQuantidade: String) {
        quantidade = novaQuantidade
    }

    /**Setter da unidade
     * @param novaUnidade Nova unidade*/
    fun onUnidadeChange(novaUnidade: String) {
        unidade = novaUnidade
    }

    /**Setter da hora
     * @param novaHora Nova hora*/
    fun onHoraChange(novaHora: String) {
        hora = novaHora
    }

    /**Função para dar reset as mensagens de feedback*/
    fun limparMensagens() {
        mensagemSucesso = null
        mensagemErro = null
    }

    fun registarAgua(perfilId: String, perfilNome: String) {
        // validação
        if (quantidade.isBlank() || unidade.isBlank()) {
            mensagemErro = "Preenche a quantidade ou unidade"
            return
        }
        isSaving = true
        viewModelScope.launch {
            try {
                val acao = buildString {
                    append("Registou a quantidade de água: $quantidade")
                    if (unidade.isNotBlank()) append(" $unidade")
                    if (hora.isNotBlank()) append(" às horas: $hora")
                }

                activityRepository.registarAtividade(
                    perfilId = perfilId,
                    perfilNome = perfilNome,
                    categoria = "AGUA",
                    acao = acao
                )
                mensagemSucesso = "Água registada com sucesso!"

                quantidade = ""
            } catch (e: Exception) {
                mensagemErro = "Erro ao registar água: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
}