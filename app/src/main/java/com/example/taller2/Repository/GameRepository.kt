
package com.example.taller2.Repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class GameRepository {

    private val database = FirebaseDatabase.getInstance()
    private val roomsReference = database.getReference("games")

    // Crear una sala nueva para dos jugadores
    fun createRoom(
        userId: String,
        userName: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Generar un código aleatorio de seis dígitos
        val roomCode = (100000..999999).random()
        val roomReference = roomsReference.child(roomCode.toString())

        // Información del jugador que crea la sala
        val playerData = mapOf(
            "id" to userId,
            "name" to userName,
            "isHost" to true
        )

        // Información inicial de la sala
        val roomData = mapOf(
            "code" to roomCode.toString(),
            "hostId" to userId,
            "status" to "WAITING",
            "createdAt" to ServerValue.TIMESTAMP,
            "players" to mapOf(
                userId to playerData
            )
        )

        // Guardar la sala directamente en Firebase
        roomReference.setValue(roomData)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onSuccess(roomCode.toString())
                } else {
                    onError(
                        task.exception?.localizedMessage
                            ?: "No se pudo crear la sala."
                    )
                }
            }
    }

    // Unirse a una sala existente
    fun joinRoom(
        roomCode: String,
        userId: String,
        userName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val code = roomCode.trim()

        if (code.isBlank()) {
            onError("Debes ingresar un código.")
            return
        }

        val roomReference = roomsReference.child(code)

        roomReference.get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {
                    onError("La sala no existe.")
                    return@addOnSuccessListener
                }

                val status = snapshot.child("status")
                    .getValue(String::class.java)

                val playersSnapshot = snapshot.child("players")
                val playersCount = playersSnapshot.childrenCount

                if (playersSnapshot.hasChild(userId)) {
                    onError("Ya estás dentro de esta sala.")
                    return@addOnSuccessListener
                }

                if (status != "WAITING") {
                    onError("Esta partida ya comenzó o no está disponible.")
                    return@addOnSuccessListener
                }

                if (playersCount >= 2) {
                    onError("La sala ya está completa.")
                    return@addOnSuccessListener
                }

                val playerData = mapOf(
                    "id" to userId,
                    "name" to userName,
                    "isHost" to false
                )

                val updates = mapOf<String, Any>(
                    "players/$userId" to playerData,
                    "status" to "READY"
                )

                roomReference.updateChildren(updates)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            onError(
                                task.exception?.localizedMessage
                                    ?: "No se pudo ingresar a la sala."
                            )
                        }
                    }
            }
            .addOnFailureListener { error ->
                onError(
                    error.localizedMessage
                        ?: "Error al buscar la sala."
                )
            }
    }
}

