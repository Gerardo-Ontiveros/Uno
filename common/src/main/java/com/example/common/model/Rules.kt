package com.example.common.model

/** Ayuda para la interfaz y la conexión falsa: ¿esta carta se puede jugar sobre la de arriba? El servidor decide al final. */
fun Card.canPlayOn(topCard: Card, activeColor: CardColor): Boolean =
    color == CardColor.WILD || color == activeColor || value == topCard.value