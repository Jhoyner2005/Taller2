
package com.example.taller2.Repository

import com.example.taller2.Model.Ficha
import com.example.taller2.Model.Poder
import com.example.taller2.Utils.Gamerulesvalidator
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

    // Escuchar las partidas disponibles en tiempo real
    fun listenToAvailableRooms(
        userId: String,
        onUpdate: (List<DataSnapshot>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val availableRooms = snapshot.children.filter { room ->

                    val status = room.child("status")
                        .getValue(String::class.java)

                    val playersSnapshot = room.child("players")
                    val playersCount = playersSnapshot.childrenCount

                    status == "WAITING" &&
                            playersCount < 2L &&
                            !playersSnapshot.hasChild(userId)
                }

                onUpdate(availableRooms)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        }

        roomsReference.addValueEventListener(listener)

        return listener
    }

    // Dejar de escuchar las partidas disponibles
    fun removeAvailableRoomsListener(
        listener: ValueEventListener
    ) {
        roomsReference.removeEventListener(listener)
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
                val powerUses = Poder.entries.associate {
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

    // Jugar una ficha en la mesa
    fun playTile(
        roomCode: String,
        userId: String,
        tile: Ficha,
        playOnLeft: Boolean,
        isComodin: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val roomReference = roomsReference.child(roomCode.trim())

        roomReference.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                onError("La sala no existe.")
                return@addOnSuccessListener
            }

            val currentTurnIndex = snapshot.child("currentTurnIndex").getValue(Int::class.java) ?: 0
            val hostId = snapshot.child("hostId").getValue(String::class.java) ?: ""
            val playerIdsOrdered = mutableListOf<String>()

            for (pSnap in snapshot.child("players").children) {
                val pId = pSnap.child("id").getValue(String::class.java) ?: pSnap.key ?: ""
                playerIdsOrdered.add(pId)
            }

            playerIdsOrdered.sortWith(Comparator { a, b ->
                if (a == hostId) -1 else if (b == hostId) 1 else a.compareTo(b)
            })

            val activeTurnUserId = if (currentTurnIndex in playerIdsOrdered.indices) playerIdsOrdered[currentTurnIndex] else ""
            if (activeTurnUserId.isNotEmpty() && activeTurnUserId != userId) {
                onError("No es tu turno de jugar.")
                return@addOnSuccessListener
            }

            val boardTilesList = mutableListOf<Ficha>()
            snapshot.child("boardTiles").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                boardTilesList.add(Ficha(a, b))
            }

            if (!Gamerulesvalidator.esMovimientoValido(tile, boardTilesList, isComodin)) {
                onError("Movimiento no válido según las reglas.")
                return@addOnSuccessListener
            }

            val adjustedTile = Gamerulesvalidator.obtenerFichaAjustada(tile, boardTilesList, playOnLeft)

            if (playOnLeft) {
                boardTilesList.add(0, adjustedTile)
            } else {
                boardTilesList.add(adjustedTile)
            }

            val playerSnap = snapshot.child("players").child(userId)
            val playerTiles = mutableListOf<Ficha>()
            var tileRemoved = false

            playerSnap.child("tiles").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                val currentTile = Ficha(a, b)
                if (!tileRemoved && (currentTile == tile || currentTile == tile.invertida())) {
                    tileRemoved = true
                } else {
                    playerTiles.add(currentTile)
                }
            }

            val currentScore = playerSnap.child("score").getValue(Int::class.java) ?: 0
            val activeEvent = snapshot.child("activeEvent").getValue(String::class.java) ?: "NINGUNO"
            val pointsGained = Gamerulesvalidator.calcularPuntajeJugada(adjustedTile, activeEvent)
            val newScore = currentScore + pointsGained

            val playersCount = playerIdsOrdered.size
            val nextTurnIndex = if (playersCount > 0) (currentTurnIndex + 1) % playersCount else 0

            fun convertTiles(tiles: List<Ficha>): List<Map<String, Int>> {
                return tiles.map { mapOf("ladoA" to it.ladoA, "ladoB" to it.ladoB) }
            }

            val updates = mutableMapOf<String, Any>()
            updates["boardTiles"] = convertTiles(boardTilesList)
            updates["players/$userId/tiles"] = convertTiles(playerTiles)
            updates["players/$userId/score"] = newScore
            updates["currentTurnIndex"] = nextTurnIndex

            if (playerTiles.isEmpty()) {
                updates["winnerId"] = userId
                updates["status"] = "FINISHED"

                // Quien gana la mano obtiene la suma de los puntos de las fichas que han quedado sin colocar por los oponentes
                var opponentUnplayedPoints = 0
                snapshot.child("players").children.forEach { otherPlayerSnap ->
                    val otherId = otherPlayerSnap.child("id").getValue(String::class.java) ?: otherPlayerSnap.key ?: ""
                    if (otherId != userId) {
                        otherPlayerSnap.child("tiles").children.forEach { tileSnap ->
                            val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                            val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                            opponentUnplayedPoints += Ficha(a, b).puntos
                        }
                    }
                }
                updates["players/$userId/score"] = newScore + opponentUnplayedPoints
            }

            roomReference.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onError(task.exception?.localizedMessage ?: "Error al jugar la ficha.")
            }
        }.addOnFailureListener { error ->
            onError(error.localizedMessage ?: "Error al conectar con la base de datos.")
        }
    }

    // Pasar turno o comer del pozo
    fun passTurn(
        roomCode: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val roomReference = roomsReference.child(roomCode.trim())

        roomReference.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                onError("La sala no existe.")
                return@addOnSuccessListener
            }

            val boardTilesList = mutableListOf<Ficha>()
            snapshot.child("boardTiles").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                boardTilesList.add(Ficha(a, b))
            }

            val playerSnap = snapshot.child("players").child(userId)
            val playerTiles = mutableListOf<Ficha>()
            playerSnap.child("tiles").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                playerTiles.add(Ficha(a, b))
            }

            // Regla: El jugador que puede colocar al menos una de sus fichas NO puede pasar ni robar del pozo.
            if (Gamerulesvalidator.tieneMovimientosPosibles(playerTiles, boardTilesList, false)) {
                onError("Tienes fichas jugables en tu mano, no puedes pasar ni robar.")
                return@addOnSuccessListener
            }

            val wellTiles = mutableListOf<Ficha>()
            snapshot.child("well").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                wellTiles.add(Ficha(a, b))
            }

            fun convertTiles(tiles: List<Ficha>): List<Map<String, Int>> {
                return tiles.map { mapOf("ladoA" to it.ladoA, "ladoB" to it.ladoB) }
            }

            val updates = mutableMapOf<String, Any>()

            if (wellTiles.isNotEmpty()) {
                val drawnTile = wellTiles.removeAt(0)
                playerTiles.add(drawnTile)

                updates["well"] = convertTiles(wellTiles)
                updates["players/$userId/tiles"] = convertTiles(playerTiles)
            } else {
                val currentTurnIndex = snapshot.child("currentTurnIndex").getValue(Int::class.java) ?: 0
                val playersCount = snapshot.child("players").childrenCount.toInt()
                val nextTurnIndex = if (playersCount > 0) (currentTurnIndex + 1) % playersCount else 0
                updates["currentTurnIndex"] = nextTurnIndex
            }

            roomReference.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onError(task.exception?.localizedMessage ?: "Error al pasar turno.")
            }
        }.addOnFailureListener { error ->
            onError(error.localizedMessage ?: "Error al conectar con la base de datos.")
        }
    }

    // Usar un poder especial: Cambiar una ficha aleatoria de la mano
    fun usePower(
        roomCode: String,
        userId: String,
        power: Poder,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val roomReference = roomsReference.child(roomCode.trim())

        roomReference.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                onError("La sala no existe.")
                return@addOnSuccessListener
            }

            val playerSnap = snapshot.child("players").child(userId)
            val currentUses = playerSnap.child("powerUses").child(power.name).getValue(Int::class.java) ?: 0

            if (currentUses <= 0) {
                onError("No te quedan usos de este poder.")
                return@addOnSuccessListener
            }

            val playerTiles = mutableListOf<Ficha>()
            playerSnap.child("tiles").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                playerTiles.add(Ficha(a, b))
            }

            if (playerTiles.isEmpty()) {
                onError("No tienes fichas en tu mano para cambiar.")
                return@addOnSuccessListener
            }

            val wellTiles = mutableListOf<Ficha>()
            snapshot.child("well").children.forEach { tileSnap ->
                val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                wellTiles.add(Ficha(a, b))
            }

            fun convertTiles(tiles: List<Ficha>): List<Map<String, Int>> {
                return tiles.map { mapOf("ladoA" to it.ladoA, "ladoB" to it.ladoB) }
            }

            val updates = mutableMapOf<String, Any>()
            updates["players/$userId/powerUses/${power.name}"] = currentUses - 1

            val currentPowersUsed = playerSnap.child("powersUsed").getValue(Int::class.java) ?: 0
            updates["players/$userId/powersUsed"] = currentPowersUsed + 1

            val randomHandIndex = playerTiles.indices.random()
            val tileToSwap = playerTiles[randomHandIndex]

            if (wellTiles.isNotEmpty()) {
                val randomWellIndex = wellTiles.indices.random()
                val newTileFromWell = wellTiles.removeAt(randomWellIndex)

                playerTiles[randomHandIndex] = newTileFromWell
                wellTiles.add(tileToSwap)

                updates["players/$userId/tiles"] = convertTiles(playerTiles)
                updates["well"] = convertTiles(wellTiles)
            } else {
                val opponentSnap = snapshot.child("players").children.firstOrNull { it.key != userId }
                val opponentId = opponentSnap?.key

                if (opponentSnap != null && opponentId != null) {
                    val opponentTiles = mutableListOf<Ficha>()
                    opponentSnap.child("tiles").children.forEach { tileSnap ->
                        val a = tileSnap.child("ladoA").getValue(Int::class.java) ?: 0
                        val b = tileSnap.child("ladoB").getValue(Int::class.java) ?: 0
                        opponentTiles.add(Ficha(a, b))
                    }

                    if (opponentTiles.isNotEmpty()) {
                        val randomOpponentIndex = opponentTiles.indices.random()
                        val newTileFromOpponent = opponentTiles[randomOpponentIndex]

                        playerTiles[randomHandIndex] = newTileFromOpponent
                        opponentTiles[randomOpponentIndex] = tileToSwap

                        updates["players/$userId/tiles"] = convertTiles(playerTiles)
                        updates["players/$opponentId/tiles"] = convertTiles(opponentTiles)
                    } else {
                        onError("No hay fichas disponibles para realizar el cambio.")
                        return@addOnSuccessListener
                    }
                } else {
                    onError("No hay fichas en el pozo para realizar el cambio.")
                    return@addOnSuccessListener
                }
            }

            roomReference.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onError(task.exception?.localizedMessage ?: "Error al usar el poder.")
            }
        }.addOnFailureListener { error ->
            onError(error.localizedMessage ?: "Error al conectar con la base de datos.")
        }
    }
}
