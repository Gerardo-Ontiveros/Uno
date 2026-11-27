package com.example.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CardColor {
    @SerialName("R") RED,
    @SerialName("G") GREEN,
    @SerialName("B") BLUE,
    @SerialName("Y") YELLOW,
    @SerialName("W") WILD
}

@Serializable
enum class CardValue {
    @SerialName("0") ZERO,
    @SerialName("1") ONE,
    @SerialName("2") TWO,
    @SerialName("3") THREE,
    @SerialName("4") FOUR,
    @SerialName("5") FIVE,
    @SerialName("6") SIX,
    @SerialName("7") SEVEN,
    @SerialName("8") EIGHT,
    @SerialName("9") NINE,
    @SerialName("skip") SKIP,
    @SerialName("reverse") REVERSE,
    @SerialName("draw2") DRAW_TWO,
    @SerialName("wild") WILD,
    @SerialName("wild4") WILD_DRAW_FOUR
}

@Serializable
data class Card(
    val id: String,
    val color: CardColor,
    val value: CardValue
)