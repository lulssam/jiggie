package com.luisamsampaio.jiggie.features.meds.ui

import com.luisamsampaio.jiggie.features.meds.MedsViewModel
import com.luisamsampaio.jiggie.features.meds.domain.AdicionarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.AlternarToma
import com.luisamsampaio.jiggie.features.meds.domain.ArquivarMedicamento
import com.luisamsampaio.jiggie.features.meds.domain.Calendario
import com.luisamsampaio.jiggie.features.meds.domain.Medicamento
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentoNovo
import com.luisamsampaio.jiggie.features.meds.domain.MedicamentosRepository
import com.luisamsampaio.jiggie.features.meds.domain.ObterMedicacaoDeHoje
import com.luisamsampaio.jiggie.features.meds.domain.TomaDada
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Um repositório em memória: faz de base de dados, sem rede. */
private class RepositorioFalso : MedicamentosRepository {
    val medicamentos = mutableListOf<Medicamento>()
    val dadas = mutableListOf<TomaDada>()

    override suspend fun ativos(caoId: String) = medicamentos.toList()

    override suspend fun tomasDadas(caoId: String, dia: LocalDate) = dadas.toList()

    override suspend fun adicionar(caoId: String, novo: MedicamentoNovo) {
        medicamentos += Medicamento(
            "m${medicamentos.size + 1}", novo.nome, novo.dose, novo.horas, novo.frequencia, novo.inicio,
        )
    }

    override suspend fun arquivar(medicamentoId: String) {
        medicamentos.removeAll { it.id == medicamentoId }
    }

    override suspend fun marcar(tomaDada: TomaDada) {
        dadas += tomaDada
    }

    override suspend fun desmarcar(tomaDada: TomaDada, dia: LocalDate) {
        dadas -= tomaDada
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MedsViewModelTest {

    private val repositorio = RepositorioFalso()
    private val calendario = Calendario { LocalDate(2026, 10, 5) }

    private fun viewModel() = MedsViewModel(
        ObterMedicacaoDeHoje(repositorio, calendario),
        AdicionarMedicamento(repositorio, calendario),
        ArquivarMedicamento(repositorio),
        AlternarToma(repositorio, calendario),
    )

    // O viewModelScope corre no Dispatchers.Main, que nos testes não existe.
    // O Unconfined corre cada launch logo ali, por isso não há esperas.
    @BeforeTest
    fun antes() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun depois() = Dispatchers.resetMain()

    @Test
    fun adicionarMostraOMedicamentoEEsvaziaOFormulario() {
        val vm = viewModel()
        vm.sincronizar(caoId = "c", versaoDosRegistos = 0)

        vm.onNome("Carprofen")
        vm.onDose("75mg")
        vm.adicionar()

        val estado = vm.state.value
        assertEquals(listOf("Carprofen"), estado.medicamentos.map { it.nome })
        assertEquals("75mg · 2× daily", estado.medicamentos.single().detalhe)
        assertEquals("", estado.formulario.nome)
    }

    @Test
    fun alternarDaETiraAToma() {
        val vm = viewModel()
        vm.sincronizar(caoId = "c", versaoDosRegistos = 0)
        vm.onNome("Carprofen")
        vm.onDose("75mg")
        vm.adicionar()

        vm.alternar("m1", vm.state.value.medicamentos.single().tomas.first())
        assertTrue(vm.state.value.medicamentos.single().tomas.first().dada)

        vm.alternar("m1", vm.state.value.medicamentos.single().tomas.first())
        assertTrue(repositorio.dadas.isEmpty())
    }
}