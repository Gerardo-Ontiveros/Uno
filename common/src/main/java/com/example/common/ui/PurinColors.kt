package com.example.common.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object PurinColors {
    val Cream = Color(0xFFFFF8E1)
    val CreamDark = Color(0xFFFFEBB3)
    val Butter = Color(0xFFFFD966)
    val Gold = Color(0xFFF7C948)
    val Caramel = Color(0xFFC98A3D)
    val Brown = Color(0xFF8B5A2B)
    val BrownDark = Color(0xFF4A2F1B)
    val Pink = Color(0xFFF6B3C2)
    val Red = Color(0xFFE5645F)
    val Green = Color(0xFF6DBE7D)
    val Blue = Color(0xFF5B9BE6)
    val Wild = Color(0xFF4A2F1B)

    val avatars = listOf(Pink, Butter, Blue, Green, Red, Caramel)

    fun avatar(index: Int): Color = avatars[index % avatars.size]

    val backgroundBrush: Brush = Brush.verticalGradient(listOf(Cream, CreamDark))
}