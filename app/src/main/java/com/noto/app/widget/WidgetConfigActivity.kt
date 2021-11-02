package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.noto.app.R
import com.noto.app.databinding.WidgetConfigActivityBinding
import com.noto.app.util.Constants
import com.noto.app.util.withBinding

class WidgetConfigActivity : AppCompatActivity() {

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WidgetConfigActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
        }
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE) {
            val appWidgetId = intent?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
            val args = bundleOf(Constants.AppWidgetId to appWidgetId)
            if (navController.currentDestination?.id != R.id.widgetConfigDialogFragment)
                navController.navigate(R.id.widgetConfigDialogFragment, args)
        }
    }
}