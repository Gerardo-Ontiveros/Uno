package com.example.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class TvUiState(
    val lobbyPlayers: List<String> = emptyList(),
    val table: ServerMessage.State? = null,
    val lastPlayedBy: String? = null
)

class TvViewModel : ViewModel() {

    // Aquí se cambia a la conexión real cuando el servidor esté listo.
    private val connection: GameConnection = FakeGameConnection(viewModelScope)

    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            connection.messages.collect { onMessage(it) }
        }
        connection.connect()
        connection.send(ClientMessage.Join("TV", Role.TV))
    }

    private fun onMessage(message: ServerMessage) {
        when (message) {
            is ServerMessage.Lobby -> _state.update { it.copy(lobbyPlayers = message.players) }
            is ServerMessage.State -> _state.update { current ->
                val previous = current.table
                val someonePlayed = previous != null && previous.topCard.id != message.topCard.id
                current.copy(
                    table = message,
                    lastPlayedBy = if (someonePlayed) previous?.turn else current.lastPlayedBy
                )
            }
            else -> Unit // lo demás es solo para los teléfonos
        }
    }

    override fun onCleared() {
        connection.disconnect()
    }
}