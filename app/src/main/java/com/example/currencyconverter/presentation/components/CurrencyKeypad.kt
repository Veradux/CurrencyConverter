package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme

@Composable
fun CurrencyKeypad(
    onDigitClick: (Char) -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 1 2 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "1",
                onClick = { onDigitClick('1') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "2",
                onClick = { onDigitClick('2') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "3",
                onClick = { onDigitClick('3') },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 4 5 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "4",
                onClick = { onDigitClick('4') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "5",
                onClick = { onDigitClick('5') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "6",
                onClick = { onDigitClick('6') },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: 7 8 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = "7",
                onClick = { onDigitClick('7') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "8",
                onClick = { onDigitClick('8') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "9",
                onClick = { onDigitClick('9') },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: . 0 ⌫
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                text = ".",
                onClick = onDecimalClick,
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "0",
                onClick = { onDigitClick('0') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "⌫",
                onClick = onBackspaceClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = when (text) {
        "." -> "Decimal point"
        "⌫" -> "Backspace"
        else -> "Digit $text"
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        shape = CurrencyConverterTheme.shapes.keypad,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = CurrencyConverterTheme.colors.keypadForeground
        )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyKeypadLightPreview() {
    CurrencyConverterTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CurrencyKeypad(
                onDigitClick = {},
                onDecimalClick = {},
                onBackspaceClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyKeypadDarkPreview() {
    CurrencyConverterTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CurrencyKeypad(
                onDigitClick = {},
                onDecimalClick = {},
                onBackspaceClick = {}
            )
        }
    }
}
