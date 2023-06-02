package com.noto.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.noto.app.NotoTheme
import com.noto.app.dialog
import com.noto.app.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private val TipHeight = 5.dp
private val TipWidth = 30.dp

@Composable
fun BaseDialogFragment.BottomSheetDialog(
    title: String,
    modifier: Modifier = Modifier,
    headerColor: Color? = null,
    painter: Painter? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val contentScrollState = rememberScrollState()
    val headerScrollState = rememberScrollState()
    val nestedScrollConnection = rememberNestedScrollInteropConnection()
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    val isContentScrolling by remember { derivedStateOf { contentScrollState.value > 0 } }
    val elevation by animateDpAsState(
        targetValue = if (isContentScrolling) NotoTheme.dimensions.extraSmall else 0.dp,
        animationSpec = tween(ElevationAnimationDuration)
    )
    NotoTheme(theme = theme) {
        Surface(
            shape = MaterialTheme.shapes.dialog,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
        ) {
            Column {
                Header(
                    title = title,
                    painter = painter,
                    headerColor = headerColor,
                    elevation = elevation,
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(headerScrollState)
                )

                Column(
                    modifier = modifier
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(contentScrollState)
                        .padding(NotoTheme.dimensions.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    painter: Painter?,
    headerColor: Color?,
    elevation: Dp,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = Modifier.zIndex(1F),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = elevation,
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .padding(NotoTheme.dimensions.medium),
            contentAlignment = Alignment.Center
        ) {
            if (painter != null) Icon(
                painter = painter,
                contentDescription = title,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Tip(headerColor ?: MaterialTheme.colorScheme.primary)
                Title(title, headerColor ?: MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun Tip(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(bottom = NotoTheme.dimensions.extraSmall)
            .size(TipWidth, TipHeight)
            .background(color, MaterialTheme.shapes.extraLarge)
    )
}

@Composable
private fun Title(title: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(top = NotoTheme.dimensions.extraSmall),
        style = MaterialTheme.typography.titleMedium,
        color = color,
    )
}