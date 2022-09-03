package com.noto.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

private const val ElevationAnimationDuration = 150

@Composable
fun NotoTopAppbar(
    title: String,
    onClick: () -> Unit,
    scrollPosition: Int,
    modifier: Modifier = Modifier,
    onNavigationIconClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val elevation by animateDpAsState(
        targetValue = if (scrollPosition > 0) NotoTheme.dimensions.extraSmall else 0.dp,
        animationSpec = tween(ElevationAnimationDuration)
    )
    SmallTopAppBar(
        title = { Text(text = title) },
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
        colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}