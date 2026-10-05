package com.luisamsampaio.jiggie.features.meds.data

import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentoNovo
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentosRepository
import com.luisamsampaio.jiggie.features.meds.domain.TomaDada
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.Clock

/**
 * O [MedicamentosRepository] a falar com o Supabase.
 *
 * É a única classe da funcionalidade que sabe que existe uma base de dados.
 * Recebe o cliente e o fuso por fora — nada de globais lá dentro — para que
 * seja quem a monta a decidir, e um teste possa montar outra coisa.
 */
internal class SupabaseMedicamentosRepository(
    private val supabase: SupabaseClient,
    private val fuso: TimeZone
) : MedicamentosRepository {
    override suspend fun ativos(caoId: String): List<Medicamento> =
        supabase.from("medicamento")
            .select(Columns.list("id", "nome", "dose", "hora", "frequencia", "inicio", "dias")) {
                filter {
                    eq("cao_id", caoId)
                    exact("arquivado_em", null)
                }
                order("nome", Order.ASCENDING)
            }
            .decodeList<MedicamentoDto>()
            .map { it.paraDominio() }

    override suspend fun tomasDadas(
        caoId: String,
        dia: LocalDate
    ): List<TomaDada> {
        val (desde, ate) = limitesDe(dia)
        return supabase.from("administracao_medicamento")
            .select(Columns.raw("medicamento_id, hora_prevista, medicamento!inner(cao_id)")) {
                filter {
                    eq("medicamento.cao_id", caoId)
                    gte("dh_medicamento", desde)
                    lt("dh_medicamento", ate)
                }
            }
            .decodeList<TomaDto>()
            .mapNotNull { it.paraDominio() }
    }

    override suspend fun adicionar(
        caoId: String,
        novo: MedicamentoNovo
    ) {
        supabase.from("medicamento").insert(novo.paraDto(caoId))
    }

    override suspend fun arquivar(medicamentoId: String) {
        supabase.from("medicamento").update({
            set("arquivado_em", Clock.System.now().toString())
        }) {
            filter { eq("id", medicamentoId) }
        }
    }

    override suspend fun marcar(tomaDada: TomaDada) {
        val dono = supabase.auth.currentUserOrNull()?.id ?: error("Sem sessão")
        supabase.from("administracao_medicamento").insert(
            TomaNovaDto(
                medicamentoId = tomaDada.medicamentoId,
                donoId = dono,
                horaPrevista = tomaDada.hora.toString(),
                quantidade = 1
            )
        )
    }

    override suspend fun desmarcar(
        tomaDada: TomaDada,
        dia: LocalDate
    ) {
        val (desde, ate) = limitesDe(dia)
        supabase.from("administracao_medicamento").delete {
            filter {
                eq("medicamento_id", tomaDada.medicamentoId)
                eq("hora_prevista", tomaDada.hora.toString())
                gte("dh_medicamento", desde)
                lt("dh_medicamento", ate)
            }
        }
    }

    /** O início deste dia e o do seguinte, no fuso de quem usa a app. */
    private fun limitesDe(dia: LocalDate): Pair<String, String> =
        dia.atStartOfDayIn(fuso).toString() to
                dia.plus(1, DateTimeUnit.DAY).atStartOfDayIn(fuso).toString()

}