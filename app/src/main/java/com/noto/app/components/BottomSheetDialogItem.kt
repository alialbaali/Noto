package com.noto.app.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.noto.app.theme.NotoTheme

@Composable
fun RowScope.BottomSheetDialogItem(
    text: String,
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rippleColor: Color = Color.Unspecified,
) {
    val alpha by animateFloatAsState(targetValue = if (enabled) 1F else 0.5F)
    Surface(
        onClick = onClick,
        modifier = Modifier
            .alpha(alpha)
            .weight(1F)
            .clip(MaterialTheme.shapes.small),
        enabled = enabled,
        rippleColor = rippleColor,
    ) {
        Column(
            modifier = modifier
                .padding(NotoTheme.dimensions.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.small),
        ) {
            Icon(painter = painter, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun ColumnScope.BottomSheetDialogItem(
    text: String,
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rippleColor: Color = Color.Unspecified,
) {
    val alpha by animateFloatAsState(targetValue = if (enabled) 1F else 0.5F)
    Surface(
        onClick = onClick,
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        enabled = enabled,
        rippleColor = rippleColor,
    ) {
        Row(
            modifier = modifier
                .padding(NotoTheme.dimensions.medium),
            horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painter = painter, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ColumnScope.BottomSheetDialogItem(
    text: String,
    onClick: () -> Unit,
    painter: Painter,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    rippleColor: Color = Color.Unspecified,
) {
    val alpha by animateFloatAsState(targetValue = if (enabled) 1F else 0.5F)
    Surface(
        onClick = onClick,
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        enabled = enabled,
        rippleColor = rippleColor,
    ) {
        Row(
            modifier = modifier
                .padding(NotoTheme.dimensions.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = painter, contentDescription = text)
                Text(text = text, style = MaterialTheme.typography.bodyLarge)
            }

            Text(text = value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
        }
    }
}