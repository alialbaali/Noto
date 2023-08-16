package com.noto.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.noto.app.settings.SettingsViewModel
import com.noto.app.theme.NotoTheme
import com.noto.app.util.navController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.LazyScreen(
    title: String,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    subtitle: String? = null,
    onNavigationIconClick: (() -> Unit)? = { navController?.navigateUp() },
    actions: @Composable RowScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(NotoTheme.dimensions.medium),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: LazyListScope.() -> Unit,
) {
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    val scope = rememberCoroutineScope()
    val isScrolling by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    NotoTheme(theme = theme) {
        Scaffold(
            topBar = {
                NotoTopAppbar(
                    title = title,
                    onClick = {
                        scope.launch {
                            state.animateScrollToItem(0)
                        }
                    },
                    isScrolling,
                    onNavigationIconClick = onNavigationIconClick,
                    actions = actions,
                    subtitle = subtitle,
                )
            },
            snackbarHost = snackbarHost
        ) { contentPadding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                state = state,
                contentPadding = PaddingValues(NotoTheme.dimensions.medium),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content,
            )
        }
    }
}