package com.luisamsampaio.jiggie.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore

class ActivityRepository {
    private val db = Firebase.firestore

    /**
     * Função para guardar uma atividade*/
    suspend fun registarAtividade(perfilId: String, categoria: String, acao: String) {
        try {
            val novaAtividade = mapOf(
                "perfilId" to perfilId,
                "categoria" to categoria,
                "acao" to acao,
                "timestamp" to FieldValue.Companion.serverTimestamp
            )

            db.collection("households")
                .document("minha_casa")
                .collection("recent_activities")
                .add(novaAtividade)

            println("Atividade guardada com sucesso!")
        } catch (e: Exception) {
            println("Erro ao guardar atividade: ${e.message}")
        }
    }
}