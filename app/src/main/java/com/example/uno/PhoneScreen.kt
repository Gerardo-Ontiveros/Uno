package com.example.uno

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.model.Card
import com.example.common.model.CardColor
import com.example.common.ui.CardView
import com.example.common.ui.PlayerAvatar
import com.example.common.ui.PurinButton
import com.example.common.ui.PurinChip
import com.example.common.ui.PurinColors
import com.example.common.ui.PurinText
import com.example.common.ui.toColor
import kotlinx.coroutines.delay

@Composable
fun PhoneScreen(viewModel: PhoneViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurinColors.backgroundBrush)
            .safeDrawingPadding()
    ) {
        when {
            !state.joined -> JoinScreen(onJoin = viewModel::join)
            state.winner != null -> GameOverScreen(
                winner = state.winner!!,
                myName = state.myName
            )
            !state.gameStarted -> LobbyScreen(
                players = state.lobbyPlayers,
                myName = state.myName,
                onStart = viewModel::start
            )
            else -> GameScreen(
                state = state,
                onPlay = viewModel::play,
                onDraw = viewModel::draw,
                onUno = viewModel::sayUno
            )
        }

        state.error?.let { message ->
            ErrorToast(
                message = message,
                onDismiss = viewModel::clearError,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
private fun JoinScreen(onJoin: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(PurinColors.Butter)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            PurinText("🍮", size = 56.sp)
        }
        Spacer(Modifier.height(20.dp))
        PurinText("UNO", size = 64.sp, weight = FontWeight.Black, color = PurinColors.Brown)
        PurinText("Edición pudín", size = 18.sp, color = PurinColors.Caramel, weight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 12) name = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurinColors.Brown,
                unfocusedBorderColor = PurinColors.Caramel,
                focusedLabelColor = PurinColors.Brown,
                cursorColor = PurinColors.Brown,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        PurinButton(
            text = "Entrar",
            onClick = { onJoin(name) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LobbyScreen(players: List<String>, myName: String, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PurinText("🍮", size = 40.sp)
            Spacer(Modifier.size(12.dp))
            Column {
                PurinText("Sala de espera", size = 26.sp, weight = FontWeight.Black)
                PurinText("De 3 a 6 jugadores", size = 15.sp, color = PurinColors.Caramel)
            }
        }
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (players.isEmpty()) {
                PurinText("Conectando…", color = PurinColors.Caramel)
            }
            players.forEachIndexed { index, player ->
                PlayerRow(
                    name = player,
                    color = PurinColors.avatar(index),
                    isMe = player == myName
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PurinButton(
            text = "Empezar partida",
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PlayerRow(name: String, color: Color, isMe: Boolean) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape)
            .clip(shape)
            .background(Color.White)
            .border(2.dp, if (isMe) PurinColors.Gold else PurinColors.CreamDark, shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(name = name, color = color, size = 44.dp)
        Spacer(Modifier.size(12.dp))
        PurinText(
            text = name,
            size = 18.sp,
            weight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        if (isMe) {
            PurinChip(text = "Tú", containerColor = PurinColors.Butter)
        }
    }
}

@Composable
private fun GameScreen(
    state: PhoneUiState,
    onPlay: (Card, CardColor?) -> Unit,
    onDraw: () -> Unit,
    onUno: () -> Unit
) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    var pendingWild by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(state.isMyTurn) {
        if (!state.isMyTurn) selectedId = null
    }
    LaunchedEffect(state.hand) {
        if (state.hand.none { it.id == selectedId }) selectedId = null
    }

    val selected = state.hand.firstOrNull { it.id == selectedId }

    val playCard: (Card) -> Unit = { card ->
        if (card.color == CardColor.WILD) pendingWild = card else onPlay(card, null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        PlayerHeader(name = state.myName, cardCount = state.hand.size)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TurnPanel(isMyTurn = state.isMyTurn, hasSelection = selected != null)
        }

        HandFan(
            cards = state.hand,
            selectedId = selectedId,
            enabled = state.isMyTurn,
            onTap = { card ->
                if (state.isMyTurn) {
                    if (selectedId == card.id) playCard(card) else selectedId = card.id
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PurinButton(
                text = "Robar",
                onClick = onDraw,
                enabled = state.isMyTurn,
                modifier = Modifier.weight(1f)
            )
            PurinButton(
                text = "Jugar",
                onClick = { selected?.let(playCard) },
                enabled = state.isMyTurn && selected != null,
                containerColor = PurinColors.Gold,
                contentColor = PurinColors.BrownDark,
                modifier = Modifier.weight(1.2f)
            )
            PurinButton(
                text = "¡UNO!",
                onClick = onUno,
                enabled = state.hand.size <= 2,
                containerColor = PurinColors.Red,
                modifier = Modifier.weight(1f)
            )
        }
    }

    pendingWild?.let { card ->
        ColorPickerDialog(
            onPick = { color ->
                onPlay(card, color)
                pendingWild = null
            },
            onDismiss = { pendingWild = null }
        )
    }
}

@Composable
private fun PlayerHeader(name: String, cardCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerAvatar(name = name, color = PurinColors.Butter, size = 44.dp, highlighted = true)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            PurinText(name, size = 18.sp, weight = FontWeight.Bold, maxLines = 1)
            PurinText("Tu mano", size = 13.sp, color = PurinColors.Caramel)
        }
        PurinChip(text = "$cardCount cartas", containerColor = Color.White)
    }
}

@Composable
private fun TurnPanel(isMyTurn: Boolean, hasSelection: Boolean) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulse"
    )
    val shape = RoundedCornerShape(32.dp)

    Column(
        modifier = Modifier
            .scale(if (isMyTurn) pulse else 1f)
            .shadow(if (isMyTurn) 10.dp else 3.dp, shape)
            .clip(shape)
            .background(if (isMyTurn) PurinColors.Butter else Color.White)
            .border(3.dp, if (isMyTurn) PurinColors.Brown else PurinColors.CreamDark, shape)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PurinText("🍮", size = 44.sp)
        Spacer(Modifier.height(6.dp))
        PurinText(
            text = if (isMyTurn) "¡Es tu turno!" else "Esperando tu turno",
            size = 26.sp,
            weight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        PurinText(
            text = when {
                isMyTurn && hasSelection -> "Toca de nuevo la carta o pulsa Jugar"
                isMyTurn -> "Elige una carta de tu mano"
                else -> "Los demás están jugando…"
            },
            size = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HandFan(
    cards: List<Card>,
    selectedId: String?,
    enabled: Boolean,
    onTap: (Card) -> Unit
) {
    val cardWidth = 80.dp
    val lift = 32.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardWidth * 1.45f + lift + 18.dp)
    ) {
        val count = cards.size
        if (count > 0) {
            val step = if (count == 1) {
                0.dp
            } else {
                ((maxWidth - cardWidth) / (count - 1)).coerceIn(14.dp, cardWidth * 0.62f)
            }
            val startX = ((maxWidth - (step * (count - 1) + cardWidth)) / 2).coerceAtLeast(0.dp)
            val totalAngle = minOf(26f, 3.5f * (count - 1))

            cards.forEachIndexed { index, card ->
                key(card.id) {
                    val t = if (count == 1) 0f else index / (count - 1f) - 0.5f
                    val arc = (t * 2f) * (t * 2f) * 14f
                    val isSelected = card.id == selectedId
                    val y by animateDpAsState(
                        targetValue = if (isSelected) 0.dp else lift + arc.dp,
                        label = "lift"
                    )

                    CardView(
                        card = card,
                        width = cardWidth,
                        enabled = enabled,
                        modifier = Modifier
                            .offset(x = startX + step * index, y = y)
                            .graphicsLayer {
                                rotationZ = t * totalAngle
                                transformOrigin = TransformOrigin(0.5f, 1.5f)
                            }
                            .zIndex(if (isSelected) 100f else index.toFloat()),
                        onClick = { onTap(card) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorToast(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(message) {
        delay(3500)
        onDismiss()
    }
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(Color(0xFFFFE3DF))
            .border(2.dp, PurinColors.Red, shape)
            .clickable(onClick = onDismiss)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        PurinText(
            text = "⚠ $message",
            size = 15.sp,
            weight = FontWeight.Bold,
            color = Color(0xFFB23A32),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ColorPickerDialog(onPick: (CardColor) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PurinColors.Cream,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                PurinText("Cancelar", color = PurinColors.Brown, weight = FontWeight.Bold)
            }
        },
        title = { PurinText("Elige un color", size = 22.sp, weight = FontWeight.Black) },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(CardColor.RED, CardColor.GREEN, CardColor.BLUE, CardColor.YELLOW)
                    .forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color.toColor())
                                .border(3.dp, Color.White, CircleShape)
                                .clickable { onPick(color) }
                        )
                    }
            }
        }
    )
}

@Composable
private fun GameOverScreen(winner: String, myName: String) {
    val won = winner == myName
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(PurinColors.Butter)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            PurinText(if (won) "🏆" else "🍮", size = 60.sp)
        }
        Spacer(Modifier.height(20.dp))
        PurinText(
            text = if (won) "¡Ganaste!" else "Ganó $winner",
            size = 34.sp,
            weight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        PurinText("Fin de la partida", size = 16.sp, color = PurinColors.Caramel)
    }
}
