package com.example.currencyconverter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import com.example.currencyconverter.presentation.theme.CurrencyConverterTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SwapCurrenciesButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(contentAlignment = Alignment.Center) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        )

        IconButton(
            onClick = onClick,
            modifier = modifier
                .size(48.dp)
                .clip(shape = CurrencyConverterTheme.shapes.circularButton)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .semantics { contentDescription = "Swap currencies" },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.SwapVert,
                contentDescription = "Swap currencies",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}