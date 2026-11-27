package com.example.uno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.model.ClientMessage
import com.example.common.model.Role
import com.example.common.model.ServerMessage
import com.example.common.net.FakeGameConnection
import com.example.common.net.GameConnection
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhoneUiState(
    val joined: Boolean = false,
    val myName: String = "",
    val lobbyPlayers: List<String> = emptyList(),
    val hand: List<Card> = emptyList(),
    val gameStarted: Boolean = false,
    val isMyTurn: Boolean = false,
    val error: String? = null,
    val winner: String? = null
)

class PhoneViewModel : ViewModel() {

    // Aquí se cambia a la conexión real cuando el servidor esté listo.
    private val connection: GameConnection = FakeGameConnection(viewModelScope)

    private val _state = MutableStateFlow(PhoneUiState())
    val state: StateFlow<PhoneUiState> = _state.asStateFlow()

    // Hay una jugada enviada y el servidor aún no responde.
    private var pendingMove = false

    init {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            connection.messages.collect { onMessage(it) }
        }
        connection.connect()
    }

    private fun onMessage(message: ServerMessage) {
        when (message) {
            is ServerMessage.Lobby -> _state.update { it.copy(lobbyPlayers = message.players) }
            is ServerMessage.Hand -> {
                pendingMove = false
                _state.update { it.copy(hand = message.cards, gameStarted = true) }
            }
            ServerMessage.YourTurn -> {
                pendingMove = false
                _state.update { it.copy(isMyTurn = true) }
            }
            is ServerMessage.Error -> {
                // Si el servidor rechazó mi jugada, sigue siendo mi turno.
                val retry = pendingMove
                pendingMove = false
                _state.update { it.copy(error = message.message, isMyTurn = it.isMyTurn || retry) }
            }
            is ServerMessage.GameOver -> _state.update {
                it.copy(winner = message.winner, isMyTurn = false)
            }
            is ServerMessage.State -> Unit // solo lo usa la TV
        }
    }

    fun join(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        connection.send(ClientMessage.Join(trimmed, Role.PHONE))
        _state.update { it.copy(joined = true, myName = trimmed) }
    }

    fun start() = connection.send(ClientMessage.Start)

    fun play(card: Card, chosenColor: CardColor? = null) {
        pendingMove = true
        connection.send(ClientMessage.Play(card.id, chosenColor))
        _state.update { it.copy(isMyTurn = false) }
    }

    fun draw() {
        pendingMove = true
        connection.send(ClientMessage.Draw)
        _state.update { it.copy(isMyTurn = false) }
    }

    fun sayUno() = connection.send(ClientMessage.Uno)

    fun clearError() = _state.update { it.copy(error = null) }

    override fun onCleared() {
        connection.disconnect()
    }
}