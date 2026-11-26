package com.example.common.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameJsonTest {

    @Test
    fun decodeState() {
        val text = """
            {"type":"state",
             "topCard":{"id":"c10","color":"B","value":"7"},
             "activeColor":"B",
             "players":[{"name":"Ana","cardCount":7},{"name":"Beto","cardCount":5}],
             "turn":"Ana",
             "direction":1,
             "deckCount":80}
        """.trimIndent()

        val message = GameJson.decode(text)

        assertTrue(message is ServerMessage.State)
        message as ServerMessage.State
        assertEquals(CardValue.SEVEN, message.topCard.value)
        assertEquals(CardColor.BLUE, message.activeColor)
        assertEquals(2, message.players.size)
        assertEquals("Ana", message.turn)
    }

    @Test
    fun decodeHandWithSpecialCards() {
        val text = """
            {"type":"hand","cards":[
              {"id":"c1","color":"R","value":"skip"},
              {"id":"c2","color":"W","value":"wild4"}
            ]}
        """.trimIndent()

        val message = GameJson.decode(text) as ServerMessage.Hand

        assertEquals(CardValue.SKIP, message.cards[0].value)
        assertEquals(CardColor.WILD, message.cards[1].color)
        assertEquals(CardValue.WILD_DRAW_FOUR, message.cards[1].value)
    }

    @Test
    fun decodeMessagesWithoutFields() {
        assertEquals(ServerMessage.YourTurn, GameJson.decode("""{"type":"yourTurn"}"""))
    }

    @Test
    fun encodeJoin() {
        val text = GameJson.encode(ClientMessage.Join("Ana", Role.PHONE))

        assertTrue(text.contains("\"type\":\"join\""))
        assertTrue(text.contains("\"name\":\"Ana\""))
        assertTrue(text.contains("\"role\":\"phone\""))
    }

    @Test
    fun encodePlayWithoutColorOmitsField() {
        val text = GameJson.encode(ClientMessage.Play(cardId = "c42"))

        assertTrue(text.contains("\"type\":\"play\""))
        assertTrue(text.contains("\"cardId\":\"c42\""))
        assertFalse(text.contains("chosenColor"))
    }

    @Test
    fun encodePlayWithColor() {
        val text = GameJson.encode(ClientMessage.Play("c9", CardColor.RED))

        assertTrue(text.contains("\"chosenColor\":\"R\""))
    }
}