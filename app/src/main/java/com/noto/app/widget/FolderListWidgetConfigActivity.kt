package com.noto.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseActivity
import com.noto.app.databinding.FolderListWidgetConfigActivityBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderListWidgetConfigActivity : BaseActivity() {

    private val viewModel by viewModel<FolderListWidgetConfigViewModel> { parametersOf(appWidgetId) }

    private val appWidgetId by lazy {
        intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (appViewModel.currentTheme == null) return
        FolderListWidgetConfigActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            setupState()
            setupListeners()
        }
    }

    private fun FolderListWidgetConfigActivityBinding.setupState() {
        setResult(Activity.RESULT_CANCELED)
        widget.lv.dividerHeight = 16.dp
        widget.lv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)
        widget.root.clipToOutline = true

        listOf(swWidgetHeader, swEditWidget, swAppIcon, swNewFolder, swNotesCount)
            .onEach { it.setupColors() }

        combine(
            viewModel.folders,
            viewModel.isNotesCountEnabled,
        ) { libraries, isShowNotesCount ->
            swNotesCount.isChecked = isShowNotesCount
            if (libraries.isEmpty()) {
                widget.lv.isVisible = false
                widget.tvPlaceholder.isVisible = true
            } else {
                widget.lv.isVisible = true
                widget.tvPlaceholder.isVisible = false
                widget.lv.adapter = FolderListWidgetAdapter(
                    this@FolderListWidgetConfigActivity,
                    libraries,
                    R.layout.folder_list_widget,
                    isShowNotesCount,
                )
            }
        }.launchIn(lifecycleScope)

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated) {
                    tb.title = stringResource(R.string.edit_folders_widget)
                    btnCreate.text = stringResource(R.string.update_widget)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.isWidgetHeaderEnabled
            .onEach { isEnabled ->
                widget.llHeader.isVisible = isEnabled
                swWidgetHeader.isChecked = isEnabled
                swAppIcon.isVisible = isEnabled
                swEditWidget.isVisible = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isEditWidgetButtonEnabled
            .onEach { isEnabled ->
                widget.llEditWidget.isVisible = isEnabled
                swEditWidget.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isAppIconEnabled
            .onEach { isEnabled ->
                widget.ivAppIcon.isVisible = isEnabled
                swAppIcon.isChecked = isEnabled
                if (isEnabled)
                    widget.tvAppName.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                else
                    widget.tvAppName.setPadding(16.dp)
            }
            .launchIn(lifecycleScope)

        viewModel.isNewFolderButtonEnabled
            .onEach { isEnabled ->
                widget.fab.isVisible = isEnabled
                swNewFolder.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.widgetRadius
            .onEach { radius ->
                sWidgetRadius.value = radius.toFloat()
                widget.ll.background = drawableResource(radius.toWidgetShapeId())
                widget.llHeader.background = drawableResource(radius.toWidgetHeaderShapeId())
            }
            .launchIn(lifecycleScope)

        viewModel.icon
            .onEach { icon -> widget.ivAppIcon.setImageResource(icon.toDrawableResourceId()) }
            .launchIn(lifecycleScope)
    }

    private fun FolderListWidgetConfigActivityBinding.setupListeners() {
        tb.setOnClickListener {
            nsv.smoothScrollTo(0, 0)
        }

        swWidgetHeader.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsWidgetHeaderEnabled(isChecked)
        }

        swEditWidget.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsEditWidgetButtonEnabled(isChecked)
        }

        swAppIcon.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsAppIconEnabled(isChecked)
        }

        swNewFolder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNewFolderButtonEnabled(isChecked)
        }

        swNotesCount.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNotesCountEnabled(isChecked)
        }

        sWidgetRadius.addOnChangeListener { _, value, _ ->
            viewModel.setWidgetRadius(value.toInt())
        }

        btnCreate.setOnClickListener {
            viewModel.createOrUpdateWidget()
            updateFolderWidget(appWidgetId)
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}