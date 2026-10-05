package com.luisamsampaio.jiggie.features.meds.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**Uma toma de hoje, dada ou por dar*/
data class TomaDeHoje(
    val hora: LocalTime,
    val dada: Boolean
)

/**Um medicamento visto no dia de hoje
 *
 * @property tomas as tomas de hoje, por hora. Vazia se hoje não toca*/
data class PlanoDeHoje(val medicamento: Medicamento, val tomas: List<TomaDeHoje>)

/** O que o ecrã dos medicamentos mostra: o dia e um plano por medicamento. */
data class MedicacaoDeHoje(val hoje: LocalDate, val planos: List<PlanoDeHoje>)

/**
 * Junta os medicamentos activos com as tomas já dadas hoje.
 *
 * Os dois pedidos vão em paralelo: nenhum dependo do outro*/
class ObterMedicacaoDeHoje(
    private val repositorio: MedicamentosRepository,
    private val calendario: Calendario
) {
    suspend operator fun invoke(caoId: String): MedicacaoDeHoje = coroutineScope {
        val hoje = calendario.hoje()
        val medicamentos = async { repositorio.ativos(caoId) }
        val dadas = async { repositorio.tomasDadas(caoId, hoje).toSet() }

        val jaDadas = dadas.await()
        MedicacaoDeHoje(
            hoje = hoje,
            planos = medicamentos.await().map { m ->
                val tomas = if (m.tocaEm(hoje)) {
                    m.horas.map { hora -> TomaDeHoje(hora, dada = TomaDada(m.id, hora) in jaDadas) }
                } else {
                    emptyList()
                }
                PlanoDeHoje(m, tomas)
            }
        )
    }
}

/**Valida e grava um medicamento novo, a começar hoje*/
class AdicionarMedicamento(
    private val repositorio: MedicamentosRepository,
    private val calendario: Calendario
) {
    suspend operator fun invoke(
        caoId: String,
        nome: String,
        dose: String,
        frequencia: Frequencia,
        vezesPorDia: Int
    ) {
        require(nome.isNotBlank()) { "A medicine needs a name" }
        require(dose.isNotBlank()) { "A medicine needs a dosage" }

        repositorio.adicionar(
            caoId,
            MedicamentoNovo(
                nome = nome.trim(),
                dose = dose.trim(),
                horas = horasPara(vezesPorDia),
                frequencia = frequencia,
                inicio = calendario.hoje()
            )
        )
    }
}

/**Tira um medicamento do plano. As tomas já dadas ficam*/
class ArquivarMedicamento(private val repositorio: MedicamentosRepository) {
    suspend operator fun invoke(medicamentoId: String) = repositorio.arquivar(medicamentoId)
}

/**
 * Dá uma toma que estaca por dar, ou desfaz uma que já estava dada.
 */
class AlternarToma(
    private val repositorio: MedicamentosRepository,
    private val calendario: Calendario
) {
    suspend operator fun invoke(medicamentoId: String, toma: TomaDeHoje) {
        val tomaDada = TomaDada(medicamentoId, toma.hora)
        if (toma.dada) repositorio.desmarcar(tomaDada, calendario.hoje())
        else repositorio.marcar(tomaDada)
    }
}