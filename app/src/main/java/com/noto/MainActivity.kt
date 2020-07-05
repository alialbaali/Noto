package com.noto

import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import com.noto.databinding.ActivityMainBinding
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.theme.observe(this) { theme ->
            when (theme) {
                SYSTEM_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                DARK_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment)

//        binding.bnv.setupWithNavController(navController)

        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//
//            if (destination.id == R.id.libraryListFragment || destination.id == R.id.homeFragment
//                || destination.id == R.id.calendarFragment) {
//
//                binding.fab.visibility = View.VISIBLE
//                binding.fab.startAnimation(slideUp)
//
//                binding.bnv.visibility = View.VISIBLE
//                binding.bnv.startAnimation(slideUp)
//
//            } else {
//
//                binding.fab.visibility = View.GONE
//                binding.fab.startAnimation(slideDown)
//
//                binding.bnv.visibility = View.GONE
//                binding.bnv.startAnimation(slideDown)
//
//            }
//        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = 0
            }
        }

    }
}