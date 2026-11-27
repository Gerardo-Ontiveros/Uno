package com.example.common.model

import kotlinx.serialization.json.Json

object GameJson {

    private val json = Json {
        classDiscriminator = "type"
        ignoreUnknownKeys = true
    }

    fun encode(message: ClientMessage): String =
        json.encodeToString(ClientMessage.serializer(), message)

    fun decode(text: String): ServerMessage =
        json.decodeFromString(ServerMessage.serializer(), text)
}