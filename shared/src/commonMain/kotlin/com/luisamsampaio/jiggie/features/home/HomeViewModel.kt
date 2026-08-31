package com.luisamsampaio.jiggie.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Gere o estado e a lógica do ecrã Home.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(isLoading = true))

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        carregar()
    }

    /**
     * Vai buscar os dados ao backend e atualiza o estado do ecrã.
     *
     * Mostra um indicador de carregamento enquanto espera,
     * e um erro se algo correr mal.
     */
    fun carregar() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // verificar se tem sessão iniciada
                val uid = supabase.auth.currentUserOrNull()?.id
                if (uid == null) {
                    _state.update { it.copy(isLoading = false, error = "No session") }
                    return@launch
                }

                val perfil = supabase.from("dono")
                    .select(
                        Columns.raw("nome, familia(nome, codigo_convite)")
                    ) {
                        filter { eq("id", uid) }
                    }
                    .decodeSingle<PerfilDto>()

                val caes = supabase.from("cao")
                    .select(Columns.list("id", "nome", "cor", "raca", "nascimento"))
                    .decodeList<CaoDto>()

                _state.update {
                    it.copy(
                        isLoading = false,
                        primeiroNome = perfil.nome.trim().substringBefore(' '),
                        nomeFamilia = perfil.familia?.nome.orEmpty(),
                        codigoFamilia = perfil.familia?.codigoConvite.orEmpty(),
                        caes = caes,
                        idCaoAtivo = it.idCaoAtivo ?: caes.firstOrNull()?.id
                    )
                }

                caes.firstOrNull()?.let { carregarDia(_state.value.idCaoAtivo ?: it.id) }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("HomeViewModel.carregar falhou: $erro")
                _state.update { it.copy(isLoading = false, error = mensagemDeErro(erro)) }
            }


        }
    }

    fun onCao(id: String) {
        _state.update { it.copy(idCaoAtivo = id) }
        carregarDia(id)
    }


    /**
     * Vai buscar os registos de hoje de um cão indicado.
     *
     * Separado do [carregar], ao trocar de cão, não precisa de voltar a ler o perfil
     * nem a lista de cães*/
    private fun carregarDia(caoId: String) {
        viewModelScope.launch {
            try {
                val fuso = TimeZone.currentSystemDefault()
                val inicio = Clock.System.todayIn(fuso).atStartOfDayIn(fuso).toString()

                val passeios = supabase.from("passeio")
                    .select(Columns.list("id", "dh_passeio", "xixi", "coco", "duracao")) {
                        filter {
                            eq("cao_id", caoId)
                            gte("dh_passeio", inicio)
                        }
                    }.decodeList<PasseioDto>()

                val refeicoes = supabase.from("comida")
                    .select(Columns.list("id", "dh_comida", "quantidade", "base", "extras")) {
                        filter {
                            eq("cao_id", caoId)
                            gte("dh_comida", inicio)
                        }
                    }.decodeList<ComidaDto>()

                val aguas = supabase.from("agua")
                    .select(Columns.list("id", "dh_agua", "quantidade")) {
                        filter {
                            eq("cao_id", caoId)
                            gte("dh_agua", inicio)
                        }
                    }.decodeList<AguaDto>()

                val sintomas = supabase.from("sintoma")
                    .select(Columns.list("id", "dh_sintoma", "tipo", "descricao", "gravidade")) {
                        filter {
                            eq("cao_id", caoId)
                            gte("dh_sintoma", inicio)
                        }
                    }.decodeList<SintomaDto>()

                val medicamentos = supabase.from("medicamento")
                    .select(Columns.list("id", "nome", "dose", "hora")) {
                        filter { eq("cao_id", caoId) }
                    }.decodeList<MedicamentoDto>()

                /** Como não há cao id nesta tabela, faz se join com a tabela dos medicamentos.*/
                val administracoes = supabase.from("administracao_medicamento")
                    .select(
                        Columns.raw(
                            "id, dh_medicamento, medicamento_id, hora_prevista, medicamento!inner(nome, dose)"
                        )
                    ) {
                        filter {
                            eq("medicamento.cao_id", caoId)
                            gte("dh_medicamento", inicio)
                        }
                    }.decodeList<AdministracaoDto>()

                val dia = resumirDia(
                    passeios, refeicoes, aguas, medicamentos, sintomas, administracoes
                )

                _state.update {
                    it.copy(
                        estadoDoDia = dia.estado,
                        recentes = dia.recentes,
                        sinalizado = dia.sinalizado
                    )
                }
            } catch (cancelamento: CancellationException) {
                throw cancelamento
            } catch (erro: Exception) {
                println("HomeViewModel.carregarDia falhou: $erro")
            }
        }
    }
}