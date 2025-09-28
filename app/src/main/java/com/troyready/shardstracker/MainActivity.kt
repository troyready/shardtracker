package com.troyready.shardstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent { MaterialTheme(colorScheme = lightColorScheme()) { AppContent() } }
    }
}

val TextFieldValueSaver =
        listSaver<TextFieldValue, Any>(
                save = { listOf(it.text, it.selection.start, it.selection.end) },
                restore = {
                    TextFieldValue(
                            it[0] as String,
                            selection = TextRange(it[1] as Int, it[2] as Int)
                    )
                }
        )

data class PlayerState(val name: TextFieldValue, val health: Int, val mastery: Int)

@Composable
fun AppContent() {
    var playerCount by rememberSaveable { mutableStateOf(2) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Initialize player states for up to 4 players
    var player1State by
            rememberSaveable(stateSaver = PlayerStateSaver) {
                mutableStateOf(PlayerState(TextFieldValue("Player 1"), 50, 0))
            }
    var player2State by
            rememberSaveable(stateSaver = PlayerStateSaver) {
                mutableStateOf(PlayerState(TextFieldValue("Player 2"), 50, 0))
            }
    var player3State by
            rememberSaveable(stateSaver = PlayerStateSaver) {
                mutableStateOf(PlayerState(TextFieldValue("Player 3"), 50, 0))
            }
    var player4State by
            rememberSaveable(stateSaver = PlayerStateSaver) {
                mutableStateOf(PlayerState(TextFieldValue("Player 4"), 50, 0))
            }

    val config = LocalConfiguration.current
    val isWide = config.screenWidthDp > 500

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Settings button in top-right corner
        IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) }

        // Main content
        when (playerCount) {
            2 ->
                    TwoPlayerLayout(
                            isWide,
                            player1State,
                            { player1State = it },
                            player2State,
                            { player2State = it }
                    )
            3 ->
                    ThreePlayerLayout(
                            isWide,
                            player1State,
                            { player1State = it },
                            player2State,
                            { player2State = it },
                            player3State,
                            { player3State = it }
                    )
            4 ->
                    FourPlayerLayout(
                            isWide,
                            player1State,
                            { player1State = it },
                            player2State,
                            { player2State = it },
                            player3State,
                            { player3State = it },
                            player4State,
                            { player4State = it }
                    )
        }

        // Settings dialog
        if (showSettingsDialog) {
            SettingsDialog(
                    currentPlayerCount = playerCount,
                    onPlayerCountChange = { playerCount = it },
                    onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

@Composable
fun SettingsDialog(
        currentPlayerCount: Int,
        onPlayerCountChange: (Int) -> Unit,
        onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        "Settings",
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                )

                Text("Number of Players", color = Color.White, fontSize = 16.sp)

                Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { count ->
                        Button(
                                onClick = {
                                    onPlayerCountChange(count)
                                    onDismiss()
                                },
                                colors =
                                        if (count == currentPlayerCount) {
                                            ButtonDefaults.buttonColors(
                                                    containerColor =
                                                            MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                        }
                        ) { Text("$count") }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("Close") }
            }
        }
    }
}

@Composable
fun TwoPlayerLayout(
        isWide: Boolean,
        player1State: PlayerState,
        onPlayer1Change: (PlayerState) -> Unit,
        player2State: PlayerState,
        onPlayer2Change: (PlayerState) -> Unit
) {
    if (isWide) {
        Row(
                modifier = Modifier.fillMaxSize().padding(16.dp).padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerColumn(player1State, onPlayer1Change, showStepButtons = true)
            PlayerColumn(player2State, onPlayer2Change, showStepButtons = true)
        }
    } else {
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(16.dp)
                                .padding(top = 48.dp)
                                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerColumn(player1State, onPlayer1Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player2State, onPlayer2Change, showStepButtons = true)
        }
    }
}

@Composable
fun ThreePlayerLayout(
        isWide: Boolean,
        player1State: PlayerState,
        onPlayer1Change: (PlayerState) -> Unit,
        player2State: PlayerState,
        onPlayer2Change: (PlayerState) -> Unit,
        player3State: PlayerState,
        onPlayer3Change: (PlayerState) -> Unit
) {
    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .padding(16.dp)
                            .padding(top = 48.dp)
                            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isWide) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerColumn(
                        player1State,
                        onPlayer1Change,
                        showStepButtons = true,
                        compactMode = false
                )
                PlayerColumn(
                        player2State,
                        onPlayer2Change,
                        showStepButtons = true,
                        compactMode = false
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                PlayerColumn(
                        player3State,
                        onPlayer3Change,
                        showStepButtons = true,
                        compactMode = true
                )
            }
        } else {
            PlayerColumn(player1State, onPlayer1Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player2State, onPlayer2Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player3State, onPlayer3Change, showStepButtons = true)
        }
    }
}

@Composable
fun FourPlayerLayout(
        isWide: Boolean,
        player1State: PlayerState,
        onPlayer1Change: (PlayerState) -> Unit,
        player2State: PlayerState,
        onPlayer2Change: (PlayerState) -> Unit,
        player3State: PlayerState,
        onPlayer3Change: (PlayerState) -> Unit,
        player4State: PlayerState,
        onPlayer4Change: (PlayerState) -> Unit
) {
    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .padding(16.dp)
                            .padding(top = 48.dp)
                            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isWide) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerColumn(
                        player1State,
                        onPlayer1Change,
                        showStepButtons = false,
                        compactMode = true
                )
                PlayerColumn(
                        player2State,
                        onPlayer2Change,
                        showStepButtons = false,
                        compactMode = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerColumn(
                        player3State,
                        onPlayer3Change,
                        showStepButtons = false,
                        compactMode = true
                )
                PlayerColumn(
                        player4State,
                        onPlayer4Change,
                        showStepButtons = false,
                        compactMode = true
                )
            }
        } else {
            PlayerColumn(player1State, onPlayer1Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player2State, onPlayer2Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player3State, onPlayer3Change, showStepButtons = true)
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(player4State, onPlayer4Change, showStepButtons = true)
        }
    }
}

@Composable
fun PlayerColumn(
        state: PlayerState,
        onStateChange: (PlayerState) -> Unit,
        showStepButtons: Boolean = true,
        compactMode: Boolean = false
) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicTextField(
                value = state.name,
                onValueChange = { newName -> onStateChange(state.copy(name = newName)) },
                singleLine = true,
                textStyle =
                        LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center
                        )
        )

        if (compactMode) {
            // Horizontal layout for Health and Mastery when in compact mode
            Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Top
            ) {
                Counter(
                        "Health",
                        state.health,
                        onValueChange = { newHealth ->
                            onStateChange(state.copy(health = newHealth))
                        },
                        color = Color(0xFF00FF00),
                        step = 5,
                        min = 0,
                        max = 50,
                        showStepButtons = showStepButtons
                )
                Counter(
                        "Mastery",
                        state.mastery,
                        onValueChange = { newMastery ->
                            onStateChange(state.copy(mastery = newMastery))
                        },
                        color = Color(0xFFFFFF00),
                        step = 5,
                        min = 0,
                        max = 30,
                        showStepButtons = showStepButtons
                )
            }
        } else {
            // Vertical layout for Health and Mastery
            Counter(
                    "Health",
                    state.health,
                    onValueChange = { newHealth -> onStateChange(state.copy(health = newHealth)) },
                    color = Color(0xFF00FF00),
                    step = 5,
                    min = 0,
                    max = 50,
                    showStepButtons = showStepButtons
            )
            Counter(
                    "Mastery",
                    state.mastery,
                    onValueChange = { newMastery ->
                        onStateChange(state.copy(mastery = newMastery))
                    },
                    color = Color(0xFFFFFF00),
                    step = 5,
                    min = 0,
                    max = 30,
                    showStepButtons = showStepButtons
            )
        }
    }
}

@Composable
fun Counter(
        label: String,
        value: Int,
        onValueChange: (Int) -> Unit,
        color: Color,
        step: Int = 1,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE,
        showStepButtons: Boolean = true
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color, fontSize = 18.sp)
        Text("$value", color = color, fontSize = 32.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onValueChange((value - 1).coerceIn(min, max)) }) { Text("-") }
            if (showStepButtons)
                    Button(onClick = { onValueChange((value - step).coerceIn(min, max)) }) {
                        Text("-$step")
                    }
            if (showStepButtons)
                    Button(onClick = { onValueChange((value + step).coerceIn(min, max)) }) {
                        Text("+$step")
                    }
            Button(onClick = { onValueChange((value + 1).coerceIn(min, max)) }) { Text("+") }
        }
    }
}

val PlayerStateSaver =
        listSaver<PlayerState, Any>(
                save = {
                    listOf(
                            it.name.text,
                            it.name.selection.start,
                            it.name.selection.end,
                            it.health,
                            it.mastery
                    )
                },
                restore = {
                    PlayerState(
                            name =
                                    TextFieldValue(
                                            it[0] as String,
                                            selection = TextRange(it[1] as Int, it[2] as Int)
                                    ),
                            health = it[3] as Int,
                            mastery = it[4] as Int
                    )
                }
        )
