package com.example.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.common.model.CardColor
import com.example.common.model.PlayerInfo
import com.example.common.model.ServerMessage
import com.example.common.ui.CardBack
import com.example.common.ui.CardView
import com.example.common.ui.PlayerAvatar
import com.example.common.ui.PurinChip
import com.example.common.ui.PurinColors
import com.example.common.ui.PurinText
import com.example.common.ui.toColor

@Composable
fun TvScreen(viewModel: TvViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurinColors.backgroundBrush)
            .padding(horizontal = 48.dp, vertical = 32.dp)
    ) {
        val table = state.table
        if (table == null) {
            LobbyView(players = state.lobbyPlayers)
        } else {
            TableView(table = table, lastPlayedBy = state.lastPlayedBy)
        }
    }
}

@Composable
private fun LobbyView(players: List<String>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(PurinColors.Butter)
                    .border(5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PurinText("🍮", size = 72.sp)
            }
            Spacer(Modifier.height(16.dp))
            PurinText("UNO", size = 96.sp, weight = FontWeight.Black, color = PurinColors.Brown)
            PurinText(
                text = "Conéctate desde tu teléfono",
                size = 26.sp,
                weight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            PurinText(
                text = "y entra a la mesa",
                size = 22.sp,
                color = PurinColors.Caramel,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PurinText(
                text = "Jugadores ${players.size}/6",
                size = 30.sp,
                weight = FontWeight.Black
            )
            repeat(3) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    repeat(2) { col ->
                        val index = row * 2 + col
                        PlayerSlot(
                            name = players.getOrNull(index),
                            color = PurinColors.avatar(index),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            PurinText(
                text = "Cuando estén todos, uno empieza la partida desde su teléfono",
                size = 16.sp,
                color = PurinColors.Caramel
            )
        }
    }
}

@Composable
private fun PlayerSlot(name: String?, color: Color, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    val filled = name != null
    Row(
        modifier = modifier
            .height(72.dp)
            .clip(shape)
            .background(if (filled) Color.White else Color.White.copy(alpha = 0.45f))
            .border(2.dp, if (filled) PurinColors.Gold else PurinColors.CreamDark, shape)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (name != null) {
            PlayerAvatar(name = name, color = color, size = 48.dp)
            PurinText(name, size = 22.sp, weight = FontWeight.Bold, maxLines = 1)
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PurinColors.CreamDark)
            )
            PurinText("Esperando…", size = 18.sp, color = PurinColors.Caramel)
        }
    }
}

@Composable
private fun TableView(table: ServerMessage.State, lastPlayedBy: String?) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PurinText("🍮", size = 36.sp)
                PurinText("UNO", size = 40.sp, weight = FontWeight.Black, color = PurinColors.Brown)
            }
            PurinChip(
                text = "Turno de ${table.turn}",
                containerColor = PurinColors.Butter,
                size = 24.sp
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CardBack(width = 110.dp)
                Spacer(Modifier.height(10.dp))
                PurinText("${table.deckCount} en el mazo", size = 18.sp, weight = FontWeight.Bold)
            }

            Spacer(Modifier.width(72.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CardView(card = table.topCard, width = 150.dp)
                Spacer(Modifier.height(10.dp))
                PurinText(
                    text = lastPlayedBy?.let { "Jugó $it" } ?: " ",
                    size = 20.sp,
                    weight = FontWeight.Bold,
                    color = PurinColors.Brown
                )
            }

            Spacer(Modifier.width(72.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(table.activeColor.toColor())
                            .border(4.dp, Color.White, CircleShape)
                    )
                    Spacer(Modifier.height(6.dp))
                    PurinText(
                        text = table.activeColor.spanishName(),
                        size = 18.sp,
                        weight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PurinText(
                        text = if (table.direction >= 0) "↻" else "↺",
                        size = 48.sp,
                        weight = FontWeight.Black,
                        color = PurinColors.Brown
                    )
                    PurinText(
                        text = if (table.direction >= 0) "Horario" else "Antihorario",
                        size = 16.sp,
                        color = PurinColors.Caramel
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            table.players.forEachIndexed { index, player ->
                PlayerTile(
                    player = player,
                    color = PurinColors.avatar(index),
                    isTurn = player.name == table.turn,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PlayerTile(
    player: PlayerInfo,
    color: Color,
    isTurn: Boolean,
    modifier: Modifier = Modifier
) {
    val background by animateColorAsState(
        targetValue = if (isTurn) PurinColors.Butter else Color.White,
        label = "tile"
    )
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .shadow(if (isTurn) 12.dp else 3.dp, shape)
            .clip(shape)
            .background(background)
            .border(
                width = if (isTurn) 4.dp else 2.dp,
                color = if (isTurn) PurinColors.Brown else PurinColors.CreamDark,
                shape = shape
            )
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerAvatar(name = player.name, color = color, size = 56.dp, highlighted = isTurn)
        Spacer(Modifier.height(8.dp))
        PurinText(player.name, size = 20.sp, weight = FontWeight.Bold, maxLines = 1)
        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CardBack(width = 26.dp)
            PurinText("× ${player.cardCount}", size = 20.sp, weight = FontWeight.Black)
        }
        Spacer(Modifier.height(6.dp))
        PurinText(
            text = if (isTurn) "Jugando…" else " ",
            size = 16.sp,
            weight = FontWeight.Bold,
            color = PurinColors.Brown
        )
    }
}

private fun CardColor.spanishName(): String = when (this) {
    CardColor.RED -> "Rojo"
    CardColor.GREEN -> "Verde"
    CardColor.BLUE -> "Azul"
    CardColor.YELLOW -> "Amarillo"
    CardColor.WILD -> "Comodín"
}