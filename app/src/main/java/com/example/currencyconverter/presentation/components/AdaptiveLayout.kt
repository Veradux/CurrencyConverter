package com.example.currencyconverter.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

/**
 * Adaptive layout that switches between narrow (stacked) and wide (side-by-side) based on
 * screen orientation and width.
 *
 * @param modifier Modifier for the outer container.
 * @param wideThresholdDp Minimum width in dp to trigger wide layout.
 * @param narrowLeftContent Content for the top area in narrow mode, left area in wide mode.
 * @param rightContent Content for the bottom area in narrow mode, right area in wide mode.
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    wideThresholdDp: Dp = 600.dp,
    narrowLeftContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Use BoxWithConstraints-like approach: we just check orientation.
    // For width-based detection, we use LocalConfiguration.screenWidthDp.
    val screenWidthDp = configuration.screenWidthDp.dp
    val isWide = isLandscape || screenWidthDp > wideThresholdDp

    if (isWide) {
        Row(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                narrowLeftContent()
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                rightContent()
            }
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                narrowLeftContent()
            }
            rightContent()
        }
    }
}
