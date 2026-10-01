package com.luisamsampaio.jiggie.features.log.administracao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.features.home.hora12
import com.luisamsampaio.jiggie.features.meds.domain.AlternarToma
import com.luisamsampaio.jiggie.features.meds.domain.ObterMedicacaoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.TomaDeHoje
import com.luisamsampaio.jiggie.mensagemDeErro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Gere o estado e a lógica do ecrã AdministracaoMeds.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class AdministracaoMedsViewModel(
    private val obterMedicacao: ObterMedicacaoDeHoje,
    private val alternarToma: AlternarToma
) : ViewModel() {

    private val _state = MutableStateFlow(AdministracaoMedsUiState())

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<AdministracaoMedsUiState> = _state.asStateFlow()


    /**
     * Vai buscar os dados ao backend e atualiza o estado do ecrã.
     *
     * Mostra um indicador de carregamento enquanto espera,
     * e um erro se algo correr mal.
     */
    fun carregar(caoId: String) {
        val atual = _state.value
        if (atual.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Só as tomas de hoje: os dias de descanso e os arquivados já
                // ficaram de fora no domínio.
                val doses = obterMedicacao(caoId).planos.flatMap { plano ->
                    plano.tomas.map { toma ->
                        DoseDoDia(
                            medicamentoId = plano.medicamento.id,
                            nome = "${plano.medicamento.nome} ${plano.medicamento.dose}",
                            hora = toma.hora,
                            horaTexto = hora12(toma.hora.hour * 60 + toma.hora.minute),
                            dada = toma.dada,
                        )
                    }
                }.sortedBy { it.hora }

                _state.update { it.copy(doses = doses, gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("carregar doses falhou: $erro")
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            } finally {
                // Também num cancelamento: sem isto, o guarda do início
                // bloqueava a folha para sempre.
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun dar(caoId: String, dose: DoseDoDia) {
        if (dose.dada) return

        viewModelScope.launch {
            try {
                // dada = false: a folha só dá tomas, nunca as desfaz
                alternarToma(dose.medicamentoId, TomaDeHoje(dose.hora, dada = false))
                carregar(caoId)
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("dar medicamento falhou: $erro")
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            }
        }
    }

    /**
     * Transforma um erro do backend numa frase legível para o utilizador.
     *
     * @param erro O erro devolvido pelo backend.
     * @return Uma mensagem em português para mostrar no ecrã.
     */
    private fun mensagem(erro: Any): String = "Erro desconhecido"
}