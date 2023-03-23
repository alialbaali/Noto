package com.noto.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import com.noto.app.NotoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.BottomSheetDialogItem(
    text: String,
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .weight(1F)
            .clip(MaterialTheme.shapes.small),
        enabled = enabled,
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