package com.luisamsampaio.jiggie.features.health

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import com.luisamsampaio.jiggie.data.repository.Atividade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sin

class HealthViewModel : ViewModel() {
    private val activityRepository = ActivityRepository()

    // lista de sintomas
    val sintomas = listOf(
        "Vómito", "Otite", "Cochear", "Sem Apetite",
        "Diarreia", "Infeção Urinária"
    )

    // sintomas selecionados pelo user
    var sintomasSelecionados by mutableStateOf<Set<String>>(emptySet())
        private set

    // notas
    var notas by mutableStateOf("")
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
                    _atividades.value = atividade.filter { it.categoria == "SAUDE" }
                }
            } catch (e: Exception) {
                println("Erro ao observar atividades: ${e.message}")

            }
        }
    }

    /**Selecionar os sintomas
     * @param sintoma Sintoma a selecionar*/
    fun onSintomaChange(sintoma: String) {
        sintomasSelecionados = if (sintomasSelecionados.contains(sintoma)) {
            sintomasSelecionados - sintoma
        } else {
            sintomasSelecionados + sintoma
        }
    }

    /**Setter das notas
     * @param novaNota Nova nota*/
    fun onNotasChange(novaNota: String) {
        notas = novaNota
    }

    /**Função para dar reset as mensagens de feedback*/
    fun limparMensagens() {
        mensagemSucesso = null
        mensagemErro = null
    }

    fun registarNotaSaude(perfilId: String, perfilNome: String) {
        // validação básica
        if (sintomasSelecionados.isEmpty()) {
            mensagemErro = "Selecione pelo menos um sintoma"
            return
        }

        isSaving = true
        viewModelScope.launch {
            try {
                val acao = buildString {
                    append("Sintomas: ${sintomasSelecionados.joinToString(", ")}")
                    if (notas.isNotBlank()) append(" | Notas: $notas")
                }

                activityRepository.registarAtividade(
                    perfilId = perfilId,
                    perfilNome = perfilNome,
                    categoria = "SAUDE",
                    acao = acao

                )
                mensagemSucesso = "Nota de saúde registada com sucesso!"

                // limpar formulário
                sintomasSelecionados = emptySet()
                notas = ""
            } catch (e: Exception) {
                mensagemErro = "Erro ao guardar: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
}