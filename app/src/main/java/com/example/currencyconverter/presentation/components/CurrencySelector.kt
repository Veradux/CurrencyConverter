package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme

@Composable
fun CurrencySelector(
    modifier: Modifier = Modifier,
    label: String,
    selectedCurrency: Currency?,
    onClick: () -> Unit
) {
    val displayText = selectedCurrency?.let {
        "${it.flagEmoji}  ${it.code.value} - ${it.displayName}"
    } ?: "Select currency"

    val contentDesc = selectedCurrency?.let {
        "$label: ${it.code.value} - ${it.displayName}"
    } ?: "$label: Select currency"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = contentDesc
                role = Role.Button
            }
            .clickable(onClick = onClick),
        shape = CurrencyConverterTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = displayText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = if (selectedCurrency != null)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencySelectorSelectedPreview() {
    CurrencyConverterTheme {
        CurrencySelector(
            label = "From",
            selectedCurrency = Currency(
                code = CurrencyCode("USD"),
                displayName = "US Dollar",
                flagEmoji = "🇺🇸"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencySelectorUnselectedPreview() {
    CurrencyConverterTheme {
        CurrencySelector(
            label = "From",
            selectedCurrency = null,
            onClick = {}
        )
    }
}
