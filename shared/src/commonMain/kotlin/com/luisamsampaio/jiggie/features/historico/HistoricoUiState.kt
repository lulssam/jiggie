package com.luisamsampaio.jiggie.features.historico

import com.luisamsampaio.jiggie.features.home.Registo
import com.luisamsampaio.jiggie.features.home.TipoDeRegisto
import kotlinx.datetime.LocalDate

/**
 * Tudo o que o ecrã Historico precisa para se mostrar corretamente.
 *
 * É imutável — quando algo muda, cria-se uma cópia nova com `.copy()`.
 * O Compose deteta essas mudanças e redesenha apenas o necessário.
 *
 *  @property filtro O tipo escolhido nos filtros. Null quer dizer todos.
 *  @property dias O que já foi carregado, agrupado por dia, do mais recente para o mais antigo.
 *  @property isLoading True durante a primeira página: o ecrã mostra o indicador grande.
 *  @property aCarregarMais True durante as páginas seguintes: só o fim da lista muda.
 *  @property haMais False quando já não há nada mais antigo para ir buscar.
 *  @property error Sem [dias], ocupa o ecrã; com dias, aparece no fim da lista.
 */
data class HistoricoUiState(
    val filtro: TipoDeRegisto? = null,
    val dias: List<DiaDoHistorico> = emptyList(),
    val isLoading: Boolean = false,
    val aCarregarMais: Boolean = false,
    val haMais: Boolean = false,
    val error: String? = null,
)

/**
 * Um dia do histórico: o titulo do cabeçalho e as linhas desse dia.*/
data class DiaDoHistorico(
    val data: LocalDate,
    val titulo: String,
    val registos: List<Registo>
)