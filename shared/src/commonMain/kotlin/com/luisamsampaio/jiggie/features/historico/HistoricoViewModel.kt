package com.luisamsampaio.jiggie.features.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisamsampaio.jiggie.features.home.Registo
import com.luisamsampaio.jiggie.features.home.TipoDeRegisto
import com.luisamsampaio.jiggie.mensagemDeErro
import com.luisamsampaio.jiggie.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.Column
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Gere o estado e a lógica do ecrã Historico.
 *
 * Vai buscar os dados necessários ao backend e guarda-os no estado
 * para o ecrã mostrar. O ecrã nunca fala diretamente com o backend —
 * passa sempre por aqui.
 */
class HistoricoViewModel : ViewModel() {

    private val _state = MutableStateFlow(HistoricoUiState(isLoading = true))

    /**
     * O estado atual do ecrã, disponível para o Composable observar.
     * Só o ViewModel pode alterar este valor — o ecrã apenas o lê.
     */
    val state: StateFlow<HistoricoUiState> = _state.asStateFlow()

    /**O cão cujo histórico está no ecrã, null quando não houver nenhum*/
    private var caoId: String? = null

    /**A versão dos registos que o ecrã já mostra. Ver [sincronizar]*/
    private var versaoCarregada = -1

    /**Tudo o que já veio da base, de todas as páginas*/
    private var linhas: List<RegistoDto> = emptyList()

    /**O pedido em curso — dentro da classe, para morrer com o vm*/
    private var pedido: Job? = null

    /**
     * Chama pelo ecrã quando o cão ativo ou a versão dos registos podem ter mudado.
     *
     * Só recarrega se algo mudou de facto: voltar ao separador sem nada novo
     * não faz pedido nenhum.
     * */
    fun sincronizar(caoId: String?, versaoDosRegistos: Int) {
        if (caoId == this.caoId && versaoDosRegistos == versaoCarregada) return
        this.caoId = caoId
        versaoCarregada = versaoDosRegistos
        recarregar()
    }

    fun onFiltro(tipo: TipoDeRegisto?) {
        if (tipo == _state.value.filtro) return
        _state.update { it.copy(filtro = tipo) }
        recarregar()
    }

    /**Começa do zero: primeira página, com o cão e o filtro atual*/
    fun recarregar() {
        pedido?.cancel()
        linhas = emptyList()

        val id = caoId
        _state.update {
            it.copy(dias = emptyList(), isLoading = id != null, haMais = false, error = null)
        }

        if (id != null) pedido = viewModelScope.launch { carregarPagina(id) }
    }

    /** A página seguinte. Não faz nada se já houver um pedido em curso ou se não houver mais. */
    fun carregarMais() {
        val id = caoId ?: return
        if (pedido?.isActive == true || !_state.value.haMais) return

        _state.update { it.copy(aCarregarMais = true, error = null) }
        pedido = viewModelScope.launch { carregarPagina(id) }
    }

    private suspend fun carregarPagina(caoId: String) {
        try {
            val filtro = _state.value.filtro
            val cursor = linhas.lastOrNull()?.let { Instant.parse(it.quando).toString() }

            val pagina = supabase.from("registo")
                .select(Columns.list("id", "tipo", "quando", "dados")) {
                    filter {
                        eq("cao_id", caoId)
                        if (filtro != null) eq("tipo", filtro.naView)
                        if (cursor != null) lt("quando", cursor)
                    }
                    order("quando", Order.DESCENDING)
                    limit(TAMANHO_DA_PAGINA.toLong())
                }
                .decodeList<RegistoDto>()


            linhas = linhas + pagina
            val fuso = TimeZone.currentSystemDefault()

            _state.update {
                it.copy(
                    dias = agruparPorDia(linhas, Clock.System.todayIn(fuso), fuso),
                    isLoading = false,
                    aCarregarMais = false,
                    haMais = pagina.size == TAMANHO_DA_PAGINA,
                )
            }
        } catch (cancelamento: CancellationException) {
            throw cancelamento
        } catch (erro: Exception) {
            println("HistoricoViewModel.carregarPagina: $erro")
            _state.update {
                it.copy(isLoading = false, aCarregarMais = false, error = mensagemDeErro(erro))
            }
        }
    }

    private companion object {
        const val TAMANHO_DA_PAGINA = 30
    }

    /**
     * Transforma um erro do backend numa frase legível para o utilizador.
     *
     * @param erro O erro devolvido pelo backend.
     * @return Uma mensagem em português para mostrar no ecrã.
     */
    private fun mensagem(erro: Any): String = "Erro desconhecido"
}