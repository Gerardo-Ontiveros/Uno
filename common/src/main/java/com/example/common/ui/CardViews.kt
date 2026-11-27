package com.example.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.model.CardValue

@Composable
fun CardView(
    card: Card,
    width: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(width * 0.14f)
    val base = card.color.toColor()
    val label = card.value.label()
    val isWild = card.color == CardColor.WILD
    val cornerColor = if (card.color == CardColor.YELLOW) PurinColors.BrownDark else Color.White
    val numberColor = if (card.color == CardColor.YELLOW) Color(0xFFD99A00) else base

    Box(
        modifier = modifier
            .size(width, width * 1.45f)
            .alpha(if (enabled) 1f else 0.62f)
            .shadow(4.dp, shape)
            .clip(shape)
            .background(base)
            .border(width * 0.04f, Color.White, shape)
            .then(
                if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick)
                else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width * 0.74f, width * 1.12f)
                .rotate(-24f)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        ) {
            if (isWild) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f)) {
                        Quarter(PurinColors.Red)
                        Quarter(PurinColors.Blue)
                    }
                    Row(modifier = Modifier.weight(1f)) {
                        Quarter(PurinColors.Green)
                        Quarter(PurinColors.Butter)
                    }
                }
            }
        }

        if (isWild) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width * 0.4f)
                    .clip(CircleShape)
                    .background(PurinColors.Wild)
                    .border(width * 0.02f, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PurinText(
                    text = label,
                    size = (width.value * 0.2f).sp,
                    weight = FontWeight.Black,
                    color = Color.White
                )
            }
        } else {
            PurinText(
                text = label,
                modifier = Modifier.align(Alignment.Center),
                size = (width.value * 0.4f).sp,
                weight = FontWeight.Black,
                color = numberColor
            )
        }

        PurinText(
            text = label,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = width * 0.1f, top = width * 0.05f),
            size = (width.value * 0.17f).sp,
            weight = FontWeight.Bold,
            color = cornerColor
        )
        PurinText(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = width * 0.1f, bottom = width * 0.05f)
                .rotate(180f),
            size = (width.value * 0.17f).sp,
            weight = FontWeight.Bold,
            color = cornerColor
        )
    }
}

@Composable
private fun RowScope.Quarter(color: Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(color)
    )
}

@Composable
fun CardBack(
    width: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(width * 0.14f)
    Box(
        modifier = modifier
            .size(width, width * 1.45f)
            .alpha(if (enabled) 1f else 0.62f)
            .shadow(4.dp, shape)
            .clip(shape)
            .background(PurinColors.Brown)
            .border(width * 0.04f, Color.White, shape)
            .then(
                if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width * 0.74f, width * 1.12f)
                .rotate(-24f)
                .clip(RoundedCornerShape(50))
                .background(PurinColors.Butter)
        )
        if (width >= 40.dp) {
            PurinText(
                text = "UNO",
                size = (width.value * 0.22f).sp,
                weight = FontWeight.Black,
                color = PurinColors.Brown
            )
        }
    }
}

fun CardColor.toColor(): Color = when (this) {
    CardColor.RED -> PurinColors.Red
    CardColor.GREEN -> PurinColors.Green
    CardColor.BLUE -> PurinColors.Blue
    CardColor.YELLOW -> PurinColors.Gold
    CardColor.WILD -> PurinColors.Wild
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