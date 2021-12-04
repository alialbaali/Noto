package com.noto.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.noto.app.databinding.TextSelectionActivityBinding
import com.noto.app.library.SelectLibraryDialogFragment
import com.noto.app.util.Constants
import com.noto.app.util.withBinding

class TextSelectionActivity : AppCompatActivity() {

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        TextSelectionActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            handleIntentContent()
        }
    }

    private fun handleIntentContent() {
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                val content = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener {
                    val args = bundleOf(Constants.LibraryId to it, Constants.Body to content)
                    navController.navigate(R.id.libraryFragment, args)
                    navController.navigate(R.id.noteFragment, args)
                }
                val args = bundleOf(Constants.LibraryId to 0L, Constants.SelectedLibraryItemClickListener to selectLibraryItemClickListener)
                navController.navigate(R.id.selectLibraryDialogFragment, args)
            }
        }
    }
}