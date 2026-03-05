package com.luisamsampaio.jiggie.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import com.luisamsampaio.jiggie.features.auth.UserProfile

class UserRepository {

    private val db = Firebase.firestore

    /**Função que vai buscar todos os perfis da familia*/
    suspend fun getPerfis(): List<UserProfile> {
        return try {

            val snapshot = db.collection("households")
                .document("minha_casa")
                .collection("profiles")
                .get()


            snapshot.documents.map { documento ->
                UserProfile(
                    id = documento.id,
                    displayName = documento.get("nome"),
                    role = documento.get("role")
                )
            }
        } catch (e: Exception) {
            println("Erro ao buscar perfis: ${e.message}")
            emptyList()
        }
    }
}