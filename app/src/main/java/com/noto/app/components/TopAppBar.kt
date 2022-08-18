package com.noto.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.noto.app.R

@Composable
fun NotoTopAppbar(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigationIconClick: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    SmallTopAppBar(
        title = { Text(text = title) },
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
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