package com.luisamsampaio.jiggie.features.meds.domain

import kotlinx.datetime.LocalDate


/**
 * O que o domínio precisa de ler e guardar sobre medicamentos.
 *
 * É só um contrato: quem o cumpre é a camada de dados, e nos
 * testes um repo falso. O domínio nunca sabe qual dos dois tem à frente.*/
interface MedicamentosRepository {

    /**Os medicamentos do cão que ainda estão no plano — sem os arquivados*/
    suspend fun ativos(caoId: String): List<Medicamento>

    /**As tomas dads no [dia], de todos os medicamentos do cão*/
    suspend fun tomasDadas(caoId: String, dia: LocalDate): List<TomaDada>

    /**Adicionar novo medicamento*/
    suspend fun adicionar(caoId: String, novo: MedicamentoNovo)

    /**Tira o medicamento do plano sem apagar as tomas já dadas*/
    suspend fun arquivar(medicamentoId: String)

    suspend fun marcar(tomaDada: TomaDada)

    suspend fun desmarcar(tomaDada: TomaDada, dia: LocalDate)

}