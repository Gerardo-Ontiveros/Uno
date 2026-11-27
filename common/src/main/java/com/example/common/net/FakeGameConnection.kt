package com.example.common.net

import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.model.CardValue
import com.example.common.model.ClientMessage
import com.example.common.model.PlayerInfo
import com.example.common.model.Role
import com.example.common.model.ServerMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Servidor falso para avanzar las pantallas sin el servidor real.
 * No valida reglas del juego: solo responde con datos de ejemplo.
 */
class FakeGameConnection(
    private val scope: CoroutineScope,
    private val delayMs: Long = 400
) : GameConnection {

    private val _messages = MutableSharedFlow<ServerMessage>(extraBufferCapacity = 64)
    override val messages: SharedFlow<ServerMessage> = _messages.asSharedFlow()

    private var playerName = "Jugador"
    private var hand = mutableListOf<Card>()
    private var nextCardId = 100

    override fun connect() = Unit

    override fun disconnect() = Unit

    override fun send(message: ClientMessage) {
        scope.launch {
            delay(delayMs)
            when (message) {
                is ClientMessage.Join -> onJoin(message)
                ClientMessage.Start -> onStart()
                is ClientMessage.Play -> onPlay(message)
                ClientMessage.Draw -> onDraw()
                ClientMessage.Uno -> Unit
            }
        }
    }

    private suspend fun onJoin(message: ClientMessage.Join) {
        playerName = message.name
        val players = if (message.role == Role.PHONE) {
            listOf(message.name, "Beto", "Caro")
        } else {
            listOf("Ana", "Beto", "Caro")
        }
        _messages.emit(ServerMessage.Lobby(players))

        if (message.role == Role.TV) {
            delay(delayMs * 5)
            _messages.emit(fakeState())
        }
    }

    private suspend fun onStart() {
        hand = startingHand()
        _messages.emit(ServerMessage.Hand(hand.toList()))
        _messages.emit(ServerMessage.YourTurn)
    }

    private suspend fun onPlay(message: ClientMessage.Play) {
        val card = hand.firstOrNull { it.id == message.cardId }
        if (card == null) {
            _messages.emit(ServerMessage.Error("Esa carta no está en tu mano"))
            return
        }
        hand.remove(card)
        _messages.emit(ServerMessage.Hand(hand.toList()))
        if (hand.isEmpty()) {
            _messages.emit(ServerMessage.GameOver(playerName))
        }
    }

    private suspend fun onDraw() {
        hand.add(Card("c${nextCardId++}", CardColor.GREEN, CardValue.THREE))
        _messages.emit(ServerMessage.Hand(hand.toList()))
    }

    private fun startingHand() = mutableListOf(
        Card("c1", CardColor.RED, CardValue.SEVEN),
        Card("c2", CardColor.BLUE, CardValue.FIVE),
        Card("c3", CardColor.GREEN, CardValue.SKIP),
        Card("c4", CardColor.YELLOW, CardValue.NINE),
        Card("c5", CardColor.RED, CardValue.DRAW_TWO),
        Card("c6", CardColor.WILD, CardValue.WILD),
        Card("c7", CardColor.BLUE, CardValue.REVERSE)
    )

    private fun fakeState() = ServerMessage.State(
        topCard = Card("c50", CardColor.BLUE, CardValue.SEVEN),
        activeColor = CardColor.BLUE,
        players = listOf(
            PlayerInfo("Ana", 7),
            PlayerInfo("Beto", 5),
            PlayerInfo("Caro", 9)
        ),
        turn = "Ana",
        direction = 1,
        deckCount = 80
    )
}