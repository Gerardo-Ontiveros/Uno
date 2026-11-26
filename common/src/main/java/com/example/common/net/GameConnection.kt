package com.example.common.net

import com.example.common.model.ClientMessage
import com.example.common.model.ServerMessage
import kotlinx.coroutines.flow.SharedFlow

interface GameConnection {

    /** Mensajes que llegan del servidor. */
    val messages: SharedFlow<ServerMessage>

    fun connect()

    fun send(message: ClientMessage)

    fun disconnect()
}