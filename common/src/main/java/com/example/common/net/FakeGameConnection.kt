package com.example.common.net

import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.model.CardValue
import com.example.common.model.ClientMessage
import com.example.common.model.PlayerInfo
import com.example.common.model.Role
import com.example.common.model.ServerMessage
import com.example.common.model.canPlayOn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Servidor falso para avanzar las pantallas sin el servidor real.
 * - Teléfono: recibe solo lo del contrato (lobby, hand, yourTurn, error, gameOver).
 * - TV: recibe una partida simulada de 4 jugadores (lobby y state).
 */
class FakeGameConnection(
    private val scope: CoroutineScope,
    private val delayMs: Long = 400
) : GameConnection {

    private val _messages = MutableSharedFlow<ServerMessage>(extraBufferCapacity = 64)
    override val messages: SharedFlow<ServerMessage> = _messages.asSharedFlow()

    private var names = listOf("Ana", "Beto", "Caro")
    private var playerName = "Jugador"
    private var hand = mutableListOf<Card>()
    private var topCard = firstTopCard
    private var activeColor = CardColor.BLUE
    private var myTurn = false
    private var botPlays = 0
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
        if (message.role == Role.PHONE) {
            playerName = message.name
            names = listOf(message.name, "Beto", "Caro")
            _messages.emit(ServerMessage.Lobby(names))
        } else {
            runTvDemo()
        }
    }

    private suspend fun onStart() {
        hand = startingHand()
        topCard = firstTopCard
        activeColor = CardColor.BLUE
        botPlays = 0
        myTurn = true
        _messages.emit(ServerMessage.Hand(hand.toList()))
        _messages.emit(ServerMessage.YourTurn)
    }

    private suspend fun onPlay(message: ClientMessage.Play) {
        if (!myTurn) {
            _messages.emit(ServerMessage.Error("No es tu turno"))
            return
        }
        val card = hand.firstOrNull { it.id == message.cardId }
        if (card == null) {
            _messages.emit(ServerMessage.Error("Esa carta no está en tu mano"))
            return
        }
        if (!card.canPlayOn(topCard, activeColor)) {
            _messages.emit(ServerMessage.Error("Esa carta no se puede jugar"))
            return
        }
        hand.remove(card)
        topCard = card
        activeColor = if (card.color == CardColor.WILD) {
            message.chosenColor ?: CardColor.RED
        } else {
            card.color
        }
        _messages.emit(ServerMessage.Hand(hand.toList()))
        if (hand.isEmpty()) {
            myTurn = false
            _messages.emit(ServerMessage.GameOver(playerName))
            return
        }
        botTurns()
    }

    private suspend fun onDraw() {
        if (!myTurn) {
            _messages.emit(ServerMessage.Error("No es tu turno"))
            return
        }
        hand.add(Card("c${nextCardId++}", CardColor.GREEN, CardValue.THREE))
        _messages.emit(ServerMessage.Hand(hand.toList()))
        botTurns()
    }

    /** Simula que los demás juegan y vuelve a darte el turno. */
    private suspend fun botTurns() {
        myTurn = false
        delay(delayMs * 6)
        val card = botCards[botPlays % botCards.size]
        botPlays++
        topCard = card
        activeColor = card.color
        myTurn = true
        _messages.emit(ServerMessage.YourTurn)
    }

    /** Para la TV: el lobby se va llenando y luego la partida avanza sola. */
    private suspend fun runTvDemo() {
        val tvNames = listOf("Ana", "Beto", "Caro", "Dani")
        for (count in 1..tvNames.size) {
            _messages.emit(ServerMessage.Lobby(tvNames.take(count)))
            delay(delayMs * 4)
        }
        delay(delayMs * 5)

        val counts = mutableListOf(7, 7, 7, 7)
        var deck = 80
        var turn = 0
        var direction = 1
        var step = 0
        var top = firstTopCard
        var color = CardColor.BLUE

        while (true) {
            _messages.emit(
                ServerMessage.State(
                    topCard = top,
                    activeColor = color,
                    players = tvNames.mapIndexed { index, name -> PlayerInfo(name, counts[index]) },
                    turn = tvNames[turn],
                    direction = direction,
                    deckCount = deck
                )
            )
            delay(delayMs * 8)

            val card = demoCards[step % demoCards.size]
            step++
            counts[turn] = if (counts[turn] > 2) counts[turn] - 1 else 7
            deck = if (deck > 20) deck - 1 else 80
            if (card.value == CardValue.REVERSE) direction = -direction
            top = card
            color = if (card.color == CardColor.WILD) CardColor.GREEN else card.color
            turn = (turn + direction + tvNames.size) % tvNames.size
        }
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
}

private val firstTopCard = Card("c50", CardColor.BLUE, CardValue.SEVEN)

private val botCards = listOf(
    Card("b1", CardColor.GREEN, CardValue.FOUR),
    Card("b2", CardColor.YELLOW, CardValue.SIX),
    Card("b3", CardColor.RED, CardValue.NINE),
    Card("b4", CardColor.BLUE, CardValue.TWO)
)

private val demoCards = listOf(
    Card("d1", CardColor.GREEN, CardValue.FOUR),
    Card("d2", CardColor.YELLOW, CardValue.SIX),
    Card("d3", CardColor.RED, CardValue.NINE),
    Card("d4", CardColor.RED, CardValue.REVERSE),
    Card("d5", CardColor.BLUE, CardValue.TWO),
    Card("d6", CardColor.WILD, CardValue.WILD),
    Card("d7", CardColor.GREEN, CardValue.DRAW_TWO),
    Card("d8", CardColor.YELLOW, CardValue.SKIP)
)