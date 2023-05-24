package com.noto.app.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme

private val IconSize = 24.dp

sealed interface SettingsItemType {
    object None : SettingsItemType

    @JvmInline
    value class Text(val value: String) : SettingsItemType

    @JvmInline
    value class Switch(val isChecked: Boolean) : SettingsItemType
}

@Composable
fun SettingsItem(
    title: String,
    type: SettingsItemType,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    painterColor: Color = Color.Unspecified,
    contentScale: ContentScale = ContentScale.Fit,
    description: String? = null,
    descriptionMaxLines: Int = Int.MAX_VALUE,
    equalWeights: Boolean = true,
) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
        Row(modifier.padding(NotoTheme.dimensions.medium), horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
            SettingsItemContent(title, titleColor, type, painter, painterColor, contentScale, description, descriptionMaxLines, equalWeights)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    title: String,
    type: SettingsItemType,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    painter: Painter? = null,
    painterColor: Color = Color.Unspecified,
    contentScale: ContentScale = ContentScale.Fit,
    description: String? = null,
    descriptionMaxLines: Int = Int.MAX_VALUE,
    equalWeights: Boolean = true,
) {
    Surface(onClick, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
        Row(modifier.padding(NotoTheme.dimensions.medium), horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
            SettingsItemContent(title, titleColor, type, painter, painterColor, contentScale, description, descriptionMaxLines, equalWeights)
        }
    }
}

@Composable
private fun RowScope.SettingsItemContent(
    title: String,
    titleColor: Color,
    type: SettingsItemType,
    painter: Painter?,
    painterColor: Color,
    contentScale: ContentScale,
    description: String?,
    descriptionMaxLines: Int,
    equalWeights: Boolean,
) {
    if (painter != null) {
        if (contentScale == ContentScale.Fit) {
            Icon(
                painter = painter,
                contentDescription = title,
                modifier = Modifier
                    .size(IconSize)
                    .clip(MaterialTheme.shapes.extraSmall),
                tint = painterColor,
            )
        } else {
            Image(
                painter = painter,
                contentDescription = title,
                modifier = Modifier
                    .size(IconSize)
                    .clip(MaterialTheme.shapes.extraSmall),
                contentScale = contentScale,
            )
        }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.small)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                modifier = if (equalWeights) Modifier.weight(1F) else Modifier,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
            )

            when (type) {
                is SettingsItemType.None -> {}
                is SettingsItemType.Text -> {
                    Spacer(Modifier.width(NotoTheme.dimensions.medium))
                    Text(
                        text = type.value,
                        style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.secondary),
                        textAlign = TextAlign.End,
                        modifier = if (equalWeights) Modifier.weight(1F) else Modifier
                    )
                }

                is SettingsItemType.Switch -> {
                    Spacer(Modifier.width(NotoTheme.dimensions.medium))
                    Switch(
                        checked = type.isChecked,
                        onCheckedChange = null,
                        modifier = Modifier.height(24.dp),
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = descriptionMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}