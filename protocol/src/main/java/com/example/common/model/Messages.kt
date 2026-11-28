package com.example.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    @SerialName("phone") PHONE,
    @SerialName("tv") TV
}

@Serializable
data class PlayerInfo(
    val name: String,
    val cardCount: Int
)

/** Mensajes que la app manda al servidor. */
@Serializable
sealed interface ClientMessage {

    @Serializable
    @SerialName("join")
    data class Join(val name: String, val role: Role) : ClientMessage

    @Serializable
    @SerialName("start")
    data object Start : ClientMessage

    @Serializable
    @SerialName("play")
    data class Play(val cardId: String, val chosenColor: CardColor? = null) : ClientMessage

    @Serializable
    @SerialName("draw")
    data object Draw : ClientMessage

    @Serializable
    @SerialName("uno")
    data object Uno : ClientMessage
}

/** Mensajes que el servidor manda a la app. */
@Serializable
sealed interface ServerMessage {

    @Serializable
    @SerialName("lobby")
    data class Lobby(val players: List<String>) : ServerMessage

    @Serializable
    @SerialName("state")
    data class State(
        val topCard: Card,
        val activeColor: CardColor,
        val players: List<PlayerInfo>,
        val turn: String,
        val direction: Int,
        val deckCount: Int
    ) : ServerMessage

    @Serializable
    @SerialName("hand")
    data class Hand(val cards: List<Card>) : ServerMessage

    @Serializable
    @SerialName("yourTurn")
    data object YourTurn : ServerMessage

    @Serializable
    @SerialName("error")
    data class Error(val message: String) : ServerMessage

    @Serializable
    @SerialName("gameOver")
    data class GameOver(val winner: String) : ServerMessage
}