package com.noto.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme
import com.noto.app.R

const val ElevationAnimationDuration = 150

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotoTopAppbar(
    title: String,
    onClick: () -> Unit,
    isScrolling: Boolean,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onNavigationIconClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val elevation by animateDpAsState(
        targetValue = if (isScrolling) NotoTheme.dimensions.extraSmall else 0.dp,
        animationSpec = tween(ElevationAnimationDuration)
    )
    TopAppBar(
        title = {
            Column {
                Text(text = title)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        },
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .shadow(elevation),
        navigationIcon = {
            if (onNavigationIconClick != null) {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_back_24),
                        contentDescription = stringResource(id = R.string.back),
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}