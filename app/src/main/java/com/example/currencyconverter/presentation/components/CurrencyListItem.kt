package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyCode
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme

@Composable
fun CurrencyListItem(
    modifier: Modifier = Modifier,
    currency: Currency,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val contentDesc = "${currency.code.value} ${currency.displayName}${if (!isEnabled) ", unavailable" else ""}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isEnabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .alpha(if (isEnabled) 1f else 0.38f)
            .semantics {
                contentDescription = contentDesc
                if (isEnabled) role = Role.Button
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currency.flagEmoji,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = currency.code.value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = currency.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyListItemEnabledPreview() {
    CurrencyConverterTheme {
        CurrencyListItem(
            currency = Currency(
                code = CurrencyCode("USD"),
                displayName = "United States Dollar",
                flagEmoji = "🇺🇸"
            ),
            isEnabled = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyListItemDisabledPreview() {
    CurrencyConverterTheme {
        CurrencyListItem(
            currency = Currency(
                code = CurrencyCode("EUR"),
                displayName = "Euro",
                flagEmoji = "🇪🇺"
            ),
            isEnabled = false,
            onClick = {}
        )
    }
}
