package com.example.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PurinText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 16.sp,
    weight: FontWeight = FontWeight.Normal,
    color: Color = PurinColors.BrownDark,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = size,
            fontWeight = weight,
            textAlign = textAlign
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines
    )
}

@Composable
fun PurinButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PurinColors.Brown,
    contentColor: Color = Color.White,
    height: Dp = 52.dp
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(height)
            .alpha(if (enabled) 1f else 0.45f)
            .shadow(if (enabled) 4.dp else 0.dp, shape)
            .clip(shape)
            .background(containerColor)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        PurinText(
            text = text,
            size = 17.sp,
            weight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
fun PurinChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = PurinColors.CreamDark,
    contentColor: Color = PurinColors.BrownDark,
    size: TextUnit = 14.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        PurinText(
            text = text,
            size = size,
            weight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
fun PlayerAvatar(
    name: String,
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(if (highlighted) 8.dp else 2.dp, CircleShape)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (highlighted) 4.dp else 3.dp,
                color = if (highlighted) PurinColors.Brown else Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        PurinText(
            text = name.take(1).uppercase(),
            size = (size.value * 0.42f).sp,
            weight = FontWeight.Black
        )
    }
}