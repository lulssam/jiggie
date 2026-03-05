package com.luisamsampaio.jiggie.features.medication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import com.luisamsampaio.jiggie.data.repository.Atividade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {
    private val activityRepository = ActivityRepository()

    var nomeRemedio by mutableStateOf("")
        private set

    var dosagem by mutableStateOf("")
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
        observaAtividades()
    }

    private fun observaAtividades() {
        viewModelScope.launch {
            try {

                activityRepository.getAtividades().collect { atividade ->
                    _atividades.value = atividade.filter { it.categoria == "REMEDIOS" }
                }
            } catch (e: Exception) {
                println("Erro ao observar atividades: ${e.message}")
            }
        }
    }


    /**Função que atualiza o nome do remédio
     * @param novoNome Novo nome do remédio*/
    fun onNomeRemedioChange(novoNome: String) {
        nomeRemedio = novoNome
    }

    /**Função para atualizar a dosagem
     * @param novaDosagem Nova dosagem*/
    fun onDosagemChange(novaDosagem: String) {
        dosagem = novaDosagem
    }

    /**
     * Função para atualizar a hora
     * @param novaHora Nova hora*/
    fun onHoraChange(novaHora: String) {
        hora = novaHora
    }

    /**Função para dar reset as mensagens de feedback*/
    fun limparMensagens() {
        mensagemSucesso = null
        mensagemErro = null
    }

    /**Função para registar remédio*/
    fun registarRemedio(perfilId: String, perfilNome: String) {
        // validação básica
        if (nomeRemedio.isBlank()) {
            mensagemErro = "Preenche o nome do remédio."
            return
        }

        isSaving = true
        viewModelScope.launch {
            try {
                val acao = buildString {
                    append("Registou o remédio '$nomeRemedio'")
                    if (dosagem.isNotBlank()) append(" - dosagem: $dosagem")
                    if (hora.isNotBlank()) append(" às horas: $hora")
                }

                activityRepository.registarAtividade(
                    perfilId = perfilId,
                    perfilNome = perfilNome,
                    categoria = "REMEDIOS",
                    acao = acao
                )

                mensagemSucesso = "Remédio registado com sucesso!"

                // limpar o formulário
                nomeRemedio = ""
                dosagem = ""
                hora = ""
            } catch (e: Exception) {
                mensagemErro = "Erro ao guardar: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
}