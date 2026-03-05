package com.luisamsampaio.jiggie.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.orderBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class Atividade(
    val id: String = "",
    val perfilId: String = "",
    val perfilNome: String = "",
    val categoria: String = "",
    val acao: String = "",
    val timestamp: Long = 0L
)

class ActivityRepository {
    private val db = Firebase.firestore

    private val householdId = "minha_casa"
    private val atividadesRef = db.collection("households")
        .document(householdId)
        .collection("recent_activities")

    /**
     * Função para guardar uma atividade*/
    suspend fun registarAtividade(
        perfilId: String,
        perfilNome: String,
        categoria: String,
        acao: String
    ) {
        try {
            val novaAtividade = mapOf(
                "perfilId" to perfilId,
                "perfilNome" to perfilNome,
                "categoria" to categoria,
                "acao" to acao,
                "timestamp" to FieldValue.Companion.serverTimestamp
            )

           /* db.collection("households")
                .document("minha_casa")
                .collection("recent_activities")
                .add(novaAtividade)
*/
            atividadesRef.add(novaAtividade)
            println("Atividade guardada com sucesso!")
        } catch (e: Exception) {
            println("Erro ao guardar atividade: ${e.message}")
        }
    }

    /**Função para is buscar as atividades recentes (em tempo real)*/
    fun getAtividades(): Flow<List<Atividade>> {
        return atividadesRef
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(10)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    Atividade(
                        id = doc.id,
                        perfilId = doc.get("perfilId"),
                        perfilNome = doc.get("perfilNome"),
                        categoria = doc.get("categoria"),
                        acao = doc.get("acao"),
                        timestamp = try {
                            doc.get("timestamp") as Long
                        } catch (e: Exception) {
                            0L
                        }
                    )
                }
            }
    }
}