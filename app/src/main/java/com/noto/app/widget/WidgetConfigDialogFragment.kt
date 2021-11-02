package com.noto.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.activity.addCallback
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.WidgetConfigDialogFragmentBinding
import com.noto.app.util.colorResource
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class WidgetConfigDialogFragment : BaseDialogFragment() {

    private val args by navArgs<WidgetConfigDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = WidgetConfigDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.create_libraries_widget)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            finishActivityWithResult(Activity.RESULT_CANCELED)
        }

        btnCreate.setOnClickListener {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val remoteViews = RemoteViews(context?.packageName, R.layout.library_list_widget).apply {
                context?.let { context ->
                    val color = when {
                        rbSystemTheme.isChecked -> context.colorResource(R.color.colorBackground)
                        rbLightTheme.isChecked -> context.colorResource(android.R.color.white)
                        rbDarkTheme.isChecked -> context.colorResource(android.R.color.black)
                        rbTransparentTheme.isChecked -> context.colorResource(android.R.color.transparent)
                        else -> 0
                    }
                    setInt(R.id.lv, "setBackgroundTint", color)
                }
            }
            appWidgetManager.updateAppWidget(args.appWidgetId, remoteViews)
            val intent = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, args.appWidgetId)
            }
            finishActivityWithResult(Activity.RESULT_OK, intent)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        finishActivityWithResult(Activity.RESULT_CANCELED)
    }

    private fun finishActivityWithResult(resultCode: Int, intent: Intent? = null) {
        activity?.setResult(resultCode, intent)
        activity?.finish()
    }

}