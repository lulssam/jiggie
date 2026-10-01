package com.luisamsampaio.jiggie.features.meds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.features.meds.domain.AdicionarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.AlternarToma
import com.luisamsampaio.jiggie.features.meds.domain.ArquivarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.ObterMedicacaoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.TomaDeHoje
import com.luisamsampaio.jiggie.features.meds.ui.paraUi
import com.luisamsampaio.jiggie.mensagemDeErro
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlin.coroutines.cancellation.CancellationException

/**
 * Gere o estado do ecrã Meds.
 *
 * Só conhece casos de uso — nem Supabase, nem DTOs, nem o repositório. É
 * isso que deixa testá-lo com um repositório falso, sem rede.
 */
class MedsViewModel(
    private val obterMedicacaoDeHoje: ObterMedicacaoDeHoje,
    private val adicionarMedicamento: AdicionarMedicamento,
    private val arquivarMedicamento: ArquivarMedicamento,
    private val alternarToma: AlternarToma
) : ViewModel() {

    private val _state = MutableStateFlow(MedsUiState(isLoading = true))

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<MedsUiState> = _state.asStateFlow()

    private var caoId: String? = null
    private var versaoCarregada = -1
    private var pedido: Job? = null

    /** Só recarrega se o cão ou os registos mudaram. */
    fun sincronizar(caoId: String?, versaoDosRegistos: Int) {
        if (caoId == this.caoId && versaoDosRegistos == versaoCarregada) return

        if (caoId != this.caoId) {
            // outro cao: os cartões do anterior não podem ficar no ecrã à espera.
            _state.update { it.copy(medicamentos = emptyList(), isLoading = caoId != null) }
        }
        this.caoId = caoId
        versaoCarregada = versaoDosRegistos
        carregar()
    }

    /**Vai buscar os medicamentos de hoje. A lista antiga fica no ecrã até chegar a nova*/
    fun carregar() {
        pedido?.cancel()
        val id = caoId ?: run {
            _state.update { it.copy(isLoading = false) }
            return
        }
        pedido = viewModelScope.launch {
            try {
                val medicacao = obterMedicacaoDeHoje(id)
                _state.update {
                    it.copy(
                        isLoading = false,
                        medicamentos = medicacao.paraUi(),
                        error = null
                    )
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("MedsViewModel.carregar falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }
        }
    }

    fun onNome(nome: String) = mudarFormulario { it.copy(nome = nome) }
    fun onDose(dose: String) = mudarFormulario { it.copy(dose = dose) }
    fun onOpcao(opcao: OpcaoDeFrequencia) = mudarFormulario { it.copy(opcao = opcao) }
    fun onVezesPorDia(vezes: Int) = mudarFormulario { it.copy(vezesPorDia = vezes) }
    fun onDia(dia: DayOfWeek) = mudarFormulario {
        it.copy(dias = if (dia in it.dias) it.dias - dia else it.dias + dia)
    }

    fun adicionar() {
        val id = caoId ?: return
        val formulario = _state.value.formulario
        if (!formulario.podeAdicionar) return

        mudarFormulario { it.copy(aGuardar = true) }
        viewModelScope.launch {
            try {
                adicionarMedicamento(
                    caoId = id,
                    nome = formulario.nome,
                    dose = formulario.dose,
                    frequencia = formulario.frequencia(),
                    vezesPorDia = formulario.vezesPorDia

                )
                _state.update { it.copy(formulario = FormularioUi()) }
                carregar()
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("MedsViewModel.adicionar falhou: $erro")
                _state.update {
                    it.copy(
                        formulario = it.formulario.copy(aGuardar = false),
                        error = mensagemDeErro(erro)
                    )
                }
            }
        }
    }

    fun arquivar(medicamentoId: String) = executar { arquivarMedicamento(medicamentoId) }

    fun alternar(medicamentoId: String, toma: TomaUi) {
        // muda já no ecrã: esperar pela rede para mostrar um certo parece lento.
        // se a base recusar, desfaz-se.
        inverter(medicamentoId, toma.hora)
        executar(desfazer = { inverter(medicamentoId, toma.hora) }) {
            alternarToma(medicamentoId, TomaDeHoje(toma.hora, toma.dada))
        }
    }

    private fun executar(desfazer: () -> Unit = {}, acao: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                acao()
                carregar()
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("MedsViewModel: acao falhou: $erro")
                desfazer()
                _state.update { it.copy(error = mensagemDeErro(erro)) }
            }
        }
    }

    private fun inverter(medicamentoId: String, hora: LocalTime) = _state.update { estado ->
        estado.copy(medicamentos = estado.medicamentos.map { m ->
            if (m.id != medicamentoId) m
            else m.copy(tomas = m.tomas.map { t -> if (t.hora == hora) t.copy(dada = !t.dada) else t })
        })
    }


    private fun mudarFormulario(mudanca: (FormularioUi) -> FormularioUi) =
        _state.update { it.copy(formulario = mudanca(it.formulario)) }

}
