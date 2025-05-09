package com.tico.calculadoracientifica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tico.calculadoracientifica.ui.theme.CalculadoracientificaTheme
import com.tico.calculadoracientifica.ui.theme.Sunglow
import com.tico.calculadoracientifica.ui.theme.Tomato
import com.tico.calculadoracientifica.ui.theme.YaleBlue
import com.tico.calculadoracientifica.ui.theme.Celadon

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoracientificaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    Column(modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                    ) {
                        CalculatorDisplay()
                        CalculatorButtons()
                    }
                }
            }
        }

    }
}

@Composable
fun CalculatorDisplay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .padding(16.dp)
    ) {
        Text(
            text = "500+",
            modifier = Modifier.fillMaxSize(),
            color = Color.Black // ou outra cor
        )
        Text(
            text = "0",
            style = MaterialTheme.typography.displayMedium,
            color = Color.Black
        )
    }
}


@Composable
fun CalculatorButtons() {
    val buttonColors = mapOf(
        "orange" to Sunglow,
        "blue" to YaleBlue,
        "red" to Tomato,
        "green" to Celadon
    )

    val buttons = listOf(
        listOf("MC", "MR", "M+", "M-", "M", "DEG"),
        listOf("2ⁿᵈ", "π", "e", "C", "⌫", "fx"),
        listOf("x²", "¹/x", "|x|", "exp", "mod", "∠"),
        listOf("√", "(", ")", "n!", "÷"),
        listOf("xʸ", "7", "8", "9", "×"),
        listOf("10ˣ", "4", "5", "6", "-"),
        listOf("log", "1", "2", "3", "+"),
        listOf("ln", "+/-", "0", ",", "=")
    )

    Column {
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        text = label,
                        modifier = Modifier.weight(1f),
                        color = when {
                            label == "=" || label == "+" || label == "-" || label == "×" || label == "÷" -> buttonColors["blue"]!!
                            label in listOf("MC", "MR", "M+", "M-", "M", "C", "⌫", "fx", "DEG") -> buttonColors["red"]!!
                            label in "0123456789.,+/-" -> buttonColors["orange"]!!
                            else -> buttonColors["green"]!!
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Button(
        onClick = { /* lógica virá depois */ },
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = text)
    }
}
