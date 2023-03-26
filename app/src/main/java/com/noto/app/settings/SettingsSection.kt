package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import com.noto.app.NotoTheme

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: @Composable (() -> Unit)? = null,
    painter: Painter? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
        if (title != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NotoTheme.dimensions.small),
                horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
            ) {
                if (painter != null) {
                    Icon(
                        painter = painter,
                        contentDescription = title,
                        modifier = Modifier.clip(MaterialTheme.shapes.extraSmall),
                        tint = Color.Unspecified,
                    )
                }

                Column(Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.extraSmall)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    )
                    if (subtitle != null) {
                        subtitle()
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
            content = content
        )
    }
}