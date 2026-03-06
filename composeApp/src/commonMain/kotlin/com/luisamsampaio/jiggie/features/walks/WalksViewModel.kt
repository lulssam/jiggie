package com.luisamsampaio.jiggie.features.walks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import com.luisamsampaio.jiggie.data.repository.Atividade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WalksViewModel : ViewModel() {

    private val activityRepository = ActivityRepository()

    var hora by mutableStateOf("")
        private set

    var duracao by mutableStateOf<Int?>(null)
        private set

    var xixi by mutableStateOf(false)
        private set

    var coco by mutableStateOf(false)
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
                    _atividades.value = atividade.filter { it.categoria == "PASSEIOS" }

                }
            } catch (e: Exception) {
                println("Erro ao observar atividades: ${e.message}")
            }
        }

    }

    /**Setter da nova hora
     * @param novaHora Nova hora*/
    fun onHoraChange(novaHora: String) {
        hora = novaHora
    }

    /**Setter da nova duração
     * @param novaDuracao Nova duração*/
    fun onDuracaoChange(novaDuracao: Int?) {
        duracao = novaDuracao
    }

    /**Setter para novo xixi
     * @param novoXixi Novo xixi*/
    fun onXixiChange(novoXixi: Boolean) {
        xixi = novoXixi
    }

    /**Setter para no cocó
     * @param novoCoco Novo cocó*/
    fun onCocoChange(novoCoco: Boolean) {
        coco = novoCoco
    }

    /**Função para dar reset as mensagens de feedback*/
    fun limparMensagens() {
        mensagemSucesso = null
        mensagemErro = null
    }

    /**Função para registar passeio*/
    fun registarPasseio(perfilId: String, perfilNome: String) {
        // validação
        if (hora.isBlank()) {
            mensagemErro = "Preenche a hora do passeio"
            return
        }

        isSaving = true
        viewModelScope.launch {
            try {
                val acao = buildString {
                    append("Passeou")
                    if (hora.isNotBlank()) append(" às $hora)")
                    if (duracao != null) append(" durante $duracao min")

                    // Integração do Xixi e Cocó na mensagem de histórico
                    if (xixi && coco) {
                        append(" e fez xixi e cocó 💦💩")
                    } else if (xixi) {
                        append(" e fez xixi 💦")
                    } else if (coco) {
                        append(" e fez cocó 💩")
                    }
                }
                activityRepository.registarAtividade(
                    perfilId = perfilId,
                    perfilNome = perfilNome,
                    categoria = "PASSEIOS",
                    acao = acao
                )

                mensagemSucesso = "Passeio registado com sucesso!"

                // limpar o formulário"
                hora = ""
                duracao = null
                xixi = false
                coco = false

            } catch (e: Exception) {
                mensagemErro = "Erro ao guardar: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
}
