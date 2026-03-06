package com.luisamsampaio.jiggie.features.charts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import com.luisamsampaio.jiggie.data.repository.Atividade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.emptyList


class ChartsViewModel : ViewModel() {
    private val activityRepository = ActivityRepository()

    var mesSelecionado by mutableIntStateOf(3) // março
        private set
    var anoSelecionado by mutableIntStateOf(2026)
        private set

    var totalAgua by mutableStateOf(0)
        private set
    var totalRemedios by mutableStateOf(0)
        private set
    var totalPasseios by mutableStateOf(0)
        private set
    var totalSaude by mutableStateOf(0)
        private set

    private val _todasAtividades = MutableStateFlow<List<Atividade>>(emptyList())

    val nomesMeses = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio",
        "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    val mesAnoLabel: String
        get() = "${nomesMeses[mesSelecionado - 1]} $anoSelecionado"

    init {
        observarAtividades()
    }

    private fun observarAtividades() {
        viewModelScope.launch {
            try {
                activityRepository.getAtividades().collect { ativadades ->
                    _todasAtividades.value = ativadades
                    atualizarEstatisticas()

                }
            } catch (e: Exception) {
                println("Erro ao observar atividades nos gráficos: ${e.message}")
            }
        }
    }

    /**Tratamento de erros para mes anterior caso seja Janeiro*/
    fun mesAnterior() {
        if (mesSelecionado == 1) {
            mesSelecionado = 12
            anoSelecionado--
        } else {
            mesSelecionado--
        }

        atualizarEstatisticas()
    }

    /**Tratamento de erros para próximo mes caso seja Dezembro*/
    fun mesSeguinte() {
        if (mesSelecionado == 12) {
            mesSelecionado = 1
            anoSelecionado++
        } else {
            mesSelecionado++
        }

        atualizarEstatisticas()
    }

    private fun atualizarEstatisticas() {
        val todas = _todasAtividades.value


        val filtradas = todas.filter { atividade ->
            if (atividade.timestamp == 0L) {
                false
            } else {
                try {
                    val instant = Instant.fromEpochMilliseconds(atividade.timestamp)
                    val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                    date.monthNumber == mesSelecionado && date.year == anoSelecionado
                } catch (e: Exception) {
                    false
                }
            }
        }

        totalAgua = filtradas.count { it.categoria == "AGUA" }
        totalRemedios = filtradas.count { it.categoria == "REMEDIOS" }
        totalPasseios = filtradas.count { it.categoria == "PASSEIOS" }
        totalSaude = filtradas.count { it.categoria == "SAUDE" }
    }
}


