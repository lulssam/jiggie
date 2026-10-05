package com.luisamsampaio.jiggie.features.meds

import com.luisamsampaio.jiggie.features.meds.data.SupabaseMedicamentosRepository
import com.luisamsampaio.jiggie.features.meds.domain.AdicionarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.AlternarToma
import com.luisamsampaio.jiggie.features.meds.domain.ArquivarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.Calendario
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentosRepository
import com.luisamsampaio.jiggie.features.meds.domain.ObterMedicacaoDeHoje
import com.luisamsampaio.jiggie.supabase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

/**
 * Monta a funcionalidade dos medicamentos.
 *
 * É a "raiz de composição": o único sítio que conhece as três camadas ao
 * mesmo tempo. Trocar o Supabase por outra coisa é mudar uma linha aqui.
 */
internal object MedsModulo {
    private val fuso = TimeZone.currentSystemDefault()
    private val calendario = Calendario { Clock.System.todayIn(fuso) }
    private val repository: MedicamentosRepository by lazy {
        SupabaseMedicamentosRepository(supabase, fuso)
    }

    /** Usados também fora daqui: pela Home e pela folha "Give medicine". */
    val obterMedicacaoDeHoje by lazy { ObterMedicacaoDeHoje(repository, calendario) }
    val alternarToma by lazy { AlternarToma(repository, calendario) }

    fun viewModel() = MedsViewModel(
        obterMedicacaoDeHoje = ObterMedicacaoDeHoje(repository, calendario),
        adicionarMedicamento = AdicionarMedicamento(repository, calendario),
        arquivarMedicamento = ArquivarMedicamento(repository),
        alternarToma = AlternarToma(repository, calendario)
    )
}