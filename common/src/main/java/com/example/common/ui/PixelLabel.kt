package com.example.common.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.common.model.CardValue

private val ShadowColor = Color(0xFFCFCFCF)

/** Número o símbolo de la carta en estilo pixel: blanco con sombra gris. */
@Composable
fun PixelLabel(value: CardValue, cell: Dp, modifier: Modifier = Modifier) {
    val glyphs = remember(value) { value.pixelCode().map { glyph(it) } }
    val columns = glyphs.sumOf { it[0].length } + (glyphs.size - 1)
    val rows = 7

    Canvas(modifier = modifier.size(cell * (columns + 1), cell * (rows + 1))) {
        val c = cell.toPx()

        fun paint(shift: Float, color: Color) {
            var x = 0
            glyphs.forEach { g ->
                g.forEachIndexed { row, line ->
                    line.forEachIndexed { col, ch ->
                        if (ch == '1') {
                            drawRect(
                                color = color,
                                topLeft = Offset((x + col) * c + shift, row * c + shift),
                                size = Size(c + 0.5f, c + 0.5f)
                            )
                        }
                    }
                }
                x += g[0].length + 1
            }
        }

        paint(c * 0.7f, ShadowColor)
        paint(0f, Color.White)
    }
}

private fun CardValue.pixelCode(): String = when (this) {
    CardValue.ZERO -> "0"
    CardValue.ONE -> "1"
    CardValue.TWO -> "2"
    CardValue.THREE -> "3"
    CardValue.FOUR -> "4"
    CardValue.FIVE -> "5"
    CardValue.SIX -> "6"
    CardValue.SEVEN -> "7"
    CardValue.EIGHT -> "8"
    CardValue.NINE -> "9"
    CardValue.SKIP -> "s"
    CardValue.REVERSE -> "r"
    CardValue.DRAW_TWO -> "+2"
    CardValue.WILD -> "w"
    CardValue.WILD_DRAW_FOUR -> "+4"
}

private fun glyph(ch: Char): List<String> = when (ch) {
    '0' -> listOf("01110", "11011", "11011", "11011", "11011", "11011", "01110")
    '1' -> listOf("00110", "01110", "11110", "00110", "00110", "00110", "11111")
    '2' -> listOf("01110", "11011", "00011", "00110", "01100", "11000", "11111")
    '3' -> listOf("11110", "00011", "00011", "01110", "00011", "00011", "11110")
    '4' -> listOf("00011", "00111", "01111", "11011", "11111", "00011", "00011")
    '5' -> listOf("11111", "11000", "11110", "00011", "00011", "11011", "01110")
    '6' -> listOf("01110", "11000", "11110", "11011", "11011", "11011", "01110")
    '7' -> listOf("11111", "00011", "00110", "00110", "01100", "01100", "01100")
    '8' -> listOf("01110", "11011", "11011", "01110", "11011", "11011", "01110")
    '9' -> listOf("01110", "11011", "11011", "01111", "00011", "00110", "01100")
    '+' -> listOf("00000", "00100", "00100", "11111", "00100", "00100", "00000")
    's' -> listOf("0011100", "0100010", "1000101", "1001001", "1010001", "0100010", "0011100")
    'r' -> listOf("0000100", "1111110", "0000100", "0000000", "0010000", "0111111", "0010000")
    'w' -> listOf("0001000", "0001000", "1111111", "0111110", "0011100", "0110110", "1100011")
    else -> listOf("00000", "00000", "00000", "00000", "00000", "00000", "00000")
}