package com.troyready.shardstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun AppContent() {
    var player1Name by
            rememberSaveable(stateSaver = TextFieldValueSaver) {
                mutableStateOf(TextFieldValue("Player 1"))
            }
    var player2Name by
            rememberSaveable(stateSaver = TextFieldValueSaver) {
                mutableStateOf(TextFieldValue("Player 2"))
            }

    var player1Health by rememberSaveable { mutableStateOf(50) }
    var player2Health by rememberSaveable { mutableStateOf(50) }
    var player1Mastery by rememberSaveable { mutableStateOf(0) }
    var player2Mastery by rememberSaveable { mutableStateOf(0) }

    val config = LocalConfiguration.current
    val isWide = config.screenWidthDp > 500 // threshold for wide layout

    if (isWide) {
        Row(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerColumn(
                    player1Name,
                    { player1Name = it },
                    player1Health,
                    { player1Health = it },
                    player1Mastery,
                    { player1Mastery = it }
            )
            PlayerColumn(
                    player2Name,
                    { player2Name = it },
                    player2Health,
                    { player2Health = it },
                    player2Mastery,
                    { player2Mastery = it }
            )
        }
    } else {
        Column(
                modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerColumn(
                    player1Name,
                    { player1Name = it },
                    player1Health,
                    { player1Health = it },
                    player1Mastery,
                    { player1Mastery = it },
                    showStepButtons = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            PlayerColumn(
                    player2Name,
                    { player2Name = it },
                    player2Health,
                    { player2Health = it },
                    player2Mastery,
                    { player2Mastery = it },
                    showStepButtons = true
            )
        }
    }
}

@Composable
fun PlayerColumn(
        name: TextFieldValue,
        onNameChange: (TextFieldValue) -> Unit,
        health: Int,
        onHealthChange: (Int) -> Unit,
        mastery: Int,
        onMasteryChange: (Int) -> Unit,
        showStepButtons: Boolean = true
) {
    Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 24.sp)
        )
        Counter(
                "Health",
                health,
                onValueChange = onHealthChange,
                color = Color(0xFF00FF00),
                step = 5,
                min = 0,
                max = 50,
                showStepButtons = showStepButtons
        )
        Counter(
                "Mastery",
                mastery,
                onValueChange = onMasteryChange,
                color = Color(0xFFFFFF00),
                step = 5,
                min = 0,
                max = 30,
                showStepButtons = showStepButtons
        )
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
