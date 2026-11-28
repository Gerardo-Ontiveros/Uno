package com.example.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.model.Card
import com.example.common.model.CardColor

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
    val isWild = card.color == CardColor.WILD
    val ring = width * 0.035f
    val inner = lerp(base, Color.White, 0.5f)
    val cell = width * 0.0286f

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
        // Óvalo inclinado: aro blanco y, adentro, un tono más claro del color de la carta.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width * 0.80f, width * 1.22f)
                .rotate(28f)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ring)
                    .clip(RoundedCornerShape(50))
                    .background(if (isWild) Color.White else inner)
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
        }

        PurinCharacter(
            size = width * 0.72f,
            modifier = Modifier.align(Alignment.Center)
        )

        PixelLabel(
            value = card.value,
            cell = cell,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = width * 0.07f, top = width * 0.05f)
        )
        PixelLabel(
            value = card.value,
            cell = cell,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = width * 0.07f, bottom = width * 0.05f)
                .rotate(180f)
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
                .rotate(28f)
                .clip(RoundedCornerShape(50))
                .background(PurinColors.Butter)
        )
        if (width >= 40.dp) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PurinCharacter(size = width * 0.56f)
                Spacer(Modifier.height(width * 0.02f))
                PurinText(
                    text = "UNO",
                    size = (width.value * 0.17f).sp,
                    weight = FontWeight.Black,
                    color = PurinColors.Brown
                )
            }
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