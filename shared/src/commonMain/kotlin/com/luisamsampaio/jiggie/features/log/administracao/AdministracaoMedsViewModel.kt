package com.luisamsampaio.jiggie.features.log.administracao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.features.home.AdministracaoDto
import com.luisamsampaio.jiggie.features.home.MedicamentoDto
import com.luisamsampaio.jiggie.features.home.hora12
import com.luisamsampaio.jiggie.features.home.minutosDaHora
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

/**
 * Gere o estado e a lógica do ecrã AdministracaoMeds.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class AdministracaoMedsViewModel : ViewModel() {

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
                val fuso = TimeZone.currentSystemDefault()
                val inicio = Clock.System
                    .todayIn(fuso)
                    .atStartOfDayIn(fuso)
                    .toString()

                val medicamentos = supabase.from("medicamento")
                    .select(Columns.list("id", "nome", "dose", "hora")) {
                        filter { eq("cao_id", caoId) }
                    }.decodeList<MedicamentoDto>()

                val dadas = supabase.from("administracao_medicamento")
                    .select(
                        Columns
                            .raw("id, dh_medicamento, medicamento_id, hora_prevista, medicamento!inner(nome, dose)")
                    ) {
                        filter {
                            eq("medicamento.cao_id", caoId)
                            gte("dh_medicamento", inicio)
                        }
                    }.decodeList<AdministracaoDto>()

                val jaDadas = dadas
                    .mapNotNull { a -> a.horaPrevista?.let { a.medicamentoId to it } }
                    .toSet()

                val doses = medicamentos.flatMap { m ->
                    m.hora.map { hora ->
                        DoseDoDia(
                            medicamentoId = m.id,
                            nome = "${m.nome} ${m.dose}",
                            hora = hora,
                            horaTexto = hora12(minutosDaHora(hora)),
                            dada = (m.id to hora) in jaDadas,
                        )
                    }
                }.sortedBy { it.hora }

                _state.update { it.copy(isLoading = false, doses = doses, gravado = true) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("carregar doses falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun dar(caoId: String, dose: DoseDoDia) {
        if (dose.dada) return

        viewModelScope.launch {
            try {
                val dono = supabase.auth.currentUserOrNull()?.id ?: return@launch

                supabase.from("administracao_medicamento").insert(
                    AdministracaoNova(
                        medicamentoId = dose.medicamentoId,
                        donoId = dono,
                        horaPrevista = dose.hora
                    )
                )

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