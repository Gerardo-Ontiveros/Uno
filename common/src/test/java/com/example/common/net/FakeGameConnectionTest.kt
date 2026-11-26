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
import org.junit.Assert
import org.junit.Test
import kotlin.collections.get

class FakeGameConnectionTest {

    @Test
    fun joinAndStartGiveLobbyHandAndTurn() = runBlocking {
        val connection = FakeGameConnection(scope = this, delayMs = 0)
        val received = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(2_000) { connection.messages.take(3).toList() }
        }

        connection.send(ClientMessage.Join("Ana", Role.PHONE))
        connection.send(ClientMessage.Start)

        val messages = received.await()
        Assert.assertTrue(messages[0] is ServerMessage.Lobby)
        val hand = messages[1] as ServerMessage.Hand
        Assert.assertEquals(7, hand.cards.size)
        Assert.assertEquals(ServerMessage.YourTurn, messages[2])
    }

    @Test
    fun playingACardRemovesItFromTheHand() = runBlocking {
        val connection = FakeGameConnection(scope = this, delayMs = 0)
        val received = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(2_000) { connection.messages.take(4).toList() }
        }

        connection.send(ClientMessage.Join("Ana", Role.PHONE))
        connection.send(ClientMessage.Start)
        connection.send(ClientMessage.Play(cardId = "c1"))

        val messages = received.await()
        val handAfter = messages[3] as ServerMessage.Hand
        Assert.assertEquals(6, handAfter.cards.size)
        Assert.assertTrue(handAfter.cards.none { it.id == "c1" })
    }
}