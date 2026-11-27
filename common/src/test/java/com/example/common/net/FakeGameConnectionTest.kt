package com.example.common.net

import com.example.common.model.ClientMessage
import com.example.common.model.Role
import com.example.common.model.ServerMessage
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeGameConnectionTest {

    @Test
    fun startGivesLobbyHandAndTurn() = runBlocking {
        val connection = FakeGameConnection(scope = this, delayMs = 0)
        val received = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(2_000) { connection.messages.take(3).toList() }
        }

        connection.send(ClientMessage.Join("Ana", Role.PHONE))
        connection.send(ClientMessage.Start)

        val messages = received.await()
        assertTrue(messages[0] is ServerMessage.Lobby)
        assertEquals(7, (messages[1] as ServerMessage.Hand).cards.size)
        assertEquals(ServerMessage.YourTurn, messages[2])
    }

    @Test
    fun playingAValidCardGivesNewHandAndNextTurn() = runBlocking {
        val connection = FakeGameConnection(scope = this, delayMs = 0)
        val received = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(2_000) { connection.messages.take(5).toList() }
        }

        connection.send(ClientMessage.Join("Ana", Role.PHONE))
        connection.send(ClientMessage.Start)
        connection.send(ClientMessage.Play(cardId = "c1"))

        val messages = received.await()
        val handAfter = messages[3] as ServerMessage.Hand
        assertEquals(6, handAfter.cards.size)
        assertTrue(handAfter.cards.none { it.id == "c1" })
        assertEquals(ServerMessage.YourTurn, messages[4])
    }

    @Test
    fun playingAnInvalidCardGivesError() = runBlocking {
        val connection = FakeGameConnection(scope = this, delayMs = 0)
        val received = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(2_000) { connection.messages.take(4).toList() }
        }

        connection.send(ClientMessage.Join("Ana", Role.PHONE))
        connection.send(ClientMessage.Start)
        connection.send(ClientMessage.Play(cardId = "c3"))

        val messages = received.await()
        assertTrue(messages[3] is ServerMessage.Error)
    }
}