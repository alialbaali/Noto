package com.noto.app.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme
import com.noto.app.dialog
import com.noto.app.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private val TipHeight = 5.dp
private val TipWidth = 30.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BaseDialogFragment.BottomSheetDialog(
    title: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val nestedScrollConnection = rememberNestedScrollInteropConnection()
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val isScrolling by remember { derivedStateOf { scrollState.value > 0 } }
    val elevation by animateDpAsState(
        targetValue = if (isScrolling) NotoTheme.dimensions.extraSmall else 0.dp,
        animationSpec = tween(ElevationAnimationDuration)
    )
    NotoTheme(theme = theme) {
        Surface(
            shape = MaterialTheme.shapes.dialog,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = modifier
                    .nestedScroll(nestedScrollConnection)
                    .verticalScroll(scrollState)
                    .padding(top = headerHeight)
                    .padding(NotoTheme.dimensions.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
            }

            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = elevation,
            ) {
                Box(
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            with(density) {
                                headerHeight = coordinates.size.height.toDp()
                            }
                        }
                        .fillMaxWidth()
                        .padding(NotoTheme.dimensions.medium),
                    contentAlignment = Alignment.Center
                ) {
                    if (painter != null) Icon(painter = painter, contentDescription = title, modifier = Modifier.align(Alignment.TopStart))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Tip()
                        Title(title)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.Tip(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(bottom = NotoTheme.dimensions.extraSmall)
            .size(TipWidth, TipHeight)
            .align(Alignment.CenterHorizontally)
            .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraLarge)
    )
}

@Composable
private fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(top = NotoTheme.dimensions.extraSmall),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}