
package com.example.taller2.Repository

import com.example.taller2.Model.Ficha
import com.example.taller2.Model.Poder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class GameRepository {

    private val database = FirebaseDatabase.getInstance()
    private val roomsReference = database.getReference("games")

    // Crear una sala para dos jugadores
    fun createRoom(
        userId: String,
        userName: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId.isBlank()) {
            onError("No se pudo identificar al jugador.")
            return
        }

        val roomCode = (100000..999999).random()
        val roomReference = roomsReference.child(roomCode.toString())

        val playerData = mapOf(
            "id" to userId,
            "name" to userName,
            "isHost" to true
        )

        val roomData = mapOf(
            "code" to roomCode.toString(),
            "hostId" to userId,
            "status" to "WAITING",
            "createdAt" to ServerValue.TIMESTAMP,
            "players" to mapOf(
                userId to playerData
            )
        )

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

        if (code.isBlank() || userId.isBlank()) {
            onError("Debes ingresar un código válido.")
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

                if (playersCount >= 2L) {
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

    // Iniciar la partida y repartir las fichas
    fun startGame(
        roomCode: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val roomReference = roomsReference.child(roomCode)

        roomReference.get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {
                    onError("La sala no existe.")
                    return@addOnSuccessListener
                }

                val hostId = snapshot.child("hostId")
                    .getValue(String::class.java)

                if (hostId != userId) {
                    onError("Solo el anfitrión puede iniciar la partida.")
                    return@addOnSuccessListener
                }

                val status = snapshot.child("status")
                    .getValue(String::class.java)

                if (status != "READY") {
                    onError("Se necesitan dos jugadores para comenzar.")
                    return@addOnSuccessListener
                }

                val playersSnapshot = snapshot.child("players")

                if (playersSnapshot.childrenCount != 2L) {
                    onError("La partida necesita exactamente dos jugadores.")
                    return@addOnSuccessListener
                }

                val playerIds = playersSnapshot.children
                    .mapNotNull { it.key }
                    .sorted()

                val orderedPlayerIds = listOf(hostId!!) +
                        playerIds.filter { it != hostId }

                // Generar las 28 fichas del dominó doble-seis
                val deck = mutableListOf<Ficha>()

                for (sideA in 0..6) {
                    for (sideB in sideA..6) {
                        deck.add(Ficha(sideA, sideB))
                    }
                }

                // Mezclar las fichas
                deck.shuffle()

                // Repartir siete fichas a cada jugador
                val firstHand = deck.take(7)
                val secondHand = deck.drop(7).take(7)

                // Guardar las fichas restantes en el pozo
                val well = deck.drop(14)

                // Configurar los poderes iniciales
                val powerUses = Poder.values().associate {
                    it.name to it.limiteUsosInicial
                }

                fun convertTiles(tiles: List<Ficha>): List<Map<String, Int>> {
                    return tiles.map { tile ->
                        mapOf(
                            "ladoA" to tile.ladoA,
                            "ladoB" to tile.ladoB
                        )
                    }
                }

                val updates = mutableMapOf<String, Any>()

                updates["status"] = "PLAYING"
                updates["well"] = convertTiles(well)
                updates["currentTurnIndex"] = 0
                updates["directionReversed"] = false
                updates["activeEvent"] = "NINGUNO"
                updates["pendingPower"] = ""
                updates["winnerId"] = ""
                updates["currentRound"] = 1
                updates["maxRounds"] = 10

                // Guardar las fichas y puntuaciones de cada jugador
                updates["players/${orderedPlayerIds[0]}/tiles"] =
                    convertTiles(firstHand)

                updates["players/${orderedPlayerIds[0]}/score"] = 0

                updates["players/${orderedPlayerIds[0]}/powerUses"] =
                    powerUses

                updates["players/${orderedPlayerIds[0]}/powersUsed"] = 0

                updates["players/${orderedPlayerIds[1]}/tiles"] =
                    convertTiles(secondHand)

                updates["players/${orderedPlayerIds[1]}/score"] = 0

                updates["players/${orderedPlayerIds[1]}/powerUses"] =
                    powerUses

                updates["players/${orderedPlayerIds[1]}/powersUsed"] = 0

                // Guardar todos los cambios en Firebase
                roomReference.updateChildren(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            onError(
                                task.exception?.localizedMessage
                                    ?: "No se pudo iniciar la partida."
                            )
                        }
                    }
            }
            .addOnFailureListener { error ->
                onError(
                    error.localizedMessage
                        ?: "Error al preparar la partida."
                )
            }
    }

    // Escuchar actualizaciones de la sala en tiempo real
    fun listenToRoom(
        roomCode: String,
        onUpdate: (DataSnapshot) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {
        val roomReference = roomsReference.child(roomCode.trim())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onUpdate(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }
        roomReference.addValueEventListener(listener)
        return listener
    }

    // Remover listener en tiempo real
    fun removeRoomListener(
        roomCode: String,
        listener: ValueEventListener
    ) {
        roomsReference.child(roomCode.trim()).removeEventListener(listener)
    }
}


