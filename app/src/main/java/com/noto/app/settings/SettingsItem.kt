package com.noto.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme

sealed interface SettingsItemType {
    object None : SettingsItemType

    @JvmInline
    value class Text(val value: String) : SettingsItemType

    @JvmInline
    value class Switch(val isChecked: Boolean) : SettingsItemType

    @JvmInline
    value class Icon(val painter: Painter) : SettingsItemType
}

@Composable
fun SettingsItem(
    title: String,
    type: SettingsItemType,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    description: String? = null,
) {
    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .background(MaterialTheme.colorScheme.surface)
            .padding(NotoTheme.dimensions.medium),
        verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.small)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = if (painter != null) Arrangement.Start else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.width(NotoTheme.dimensions.medium))
            }

            val textModifier = if (type is SettingsItemType.Text) {
                Modifier.weight(weight = 1F, fill = false)
            } else {
                Modifier
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                modifier = textModifier
            )

            if (type !is SettingsItemType.None) Spacer(Modifier.width(NotoTheme.dimensions.medium))

            when (type) {
                is SettingsItemType.None -> {}
                is SettingsItemType.Text -> Text(
                    text = type.value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.End,
                    modifier = textModifier,
                )
                is SettingsItemType.Switch -> Switch(
                    checked = type.isChecked,
                    onCheckedChange = null,
                    modifier = Modifier.height(24.dp),
                )
                is SettingsItemType.Icon -> Icon(type.painter, title, Modifier.size(24.dp), Color.Unspecified)
            }
        }

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}