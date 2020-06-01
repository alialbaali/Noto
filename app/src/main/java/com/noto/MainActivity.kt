package com.noto

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.noto.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

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