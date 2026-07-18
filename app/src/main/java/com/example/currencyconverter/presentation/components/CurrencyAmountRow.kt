package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme

@Composable
fun CurrencyAmountRow(
    modifier: Modifier = Modifier,
    amount: String,
    currencyCode: String,
    flagEmoji: String,
    label: String,
    amountColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .semantics { contentDescription = "$label: $amount $currencyCode" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = flagEmoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = amount.ifEmpty { "0" },
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (amount.isEmpty()) amountColor.copy(alpha = 0.4f) else amountColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyAmountRowWithAmountPreview() {
    CurrencyConverterTheme {
        CurrencyAmountRow(
            amount = "123.45",
            currencyCode = "USD",
            flagEmoji = "🇺🇸",
            label = "You send"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyAmountRowEmptyPreview() {
    CurrencyConverterTheme {
        CurrencyAmountRow(
            amount = "",
            currencyCode = "EUR",
            flagEmoji = "🇪🇺",
            label = "You send"
        )
    }
}
