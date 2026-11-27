package com.example.uno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.model.CardValue

@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .size(width = 84.dp, height = 124.dp)
            .clip(shape)
            .background(card.color.toColor())
            .border(3.dp, Color.White, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = card.value.label(),
            color = if (card.color == CardColor.YELLOW) Color(0xFF212121) else Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun CardColor.toColor(): Color = when (this) {
    CardColor.RED -> Color(0xFFD32F2F)
    CardColor.GREEN -> Color(0xFF388E3C)
    CardColor.BLUE -> Color(0xFF1976D2)
    CardColor.YELLOW -> Color(0xFFFBC02D)
    CardColor.WILD -> Color(0xFF212121)
}

private fun CardValue.label(): String = when (this) {
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
    CardValue.SKIP -> "⊘"
    CardValue.REVERSE -> "⇄"
    CardValue.DRAW_TWO -> "+2"
    CardValue.WILD -> "★"
    CardValue.WILD_DRAW_FOUR -> "+4"
}