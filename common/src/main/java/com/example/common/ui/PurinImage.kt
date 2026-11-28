package com.example.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

/**
 * Muestra la imagen drawable llamada "purin" (PNG con fondo transparente).
 * Si el archivo todavía no existe, muestra un 🍮.
 */
@Composable
fun PurinCharacter(size: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resId = remember {
        context.resources.getIdentifier("purin", "drawable", context.packageName)
    }

    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
    } else {
        Box(
            modifier = modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            PurinText(text = "🍮", size = (size.value * 0.7f).sp)
        }
    }
}