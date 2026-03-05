package com.luisamsampaio.jiggie.features.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.data.repository.ActivityRepository
import kotlinx.coroutines.launch

class MedicationViewModel : ViewModel() {
    private val activityRepository = ActivityRepository()

    fun marcarRemedioComoTomado(perfilId: String, nomeRemedio: String) {
        viewModelScope.launch {
            activityRepository.registarAtividade(
                perfilId = perfilId,
                categoria = "REMEDIOS",
                acao = "Marcou o remédio '${nomeRemedio}' como tomado."
            )
        }
    }
}