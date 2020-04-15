package com.noto

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
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

        binding.bottomNav.setupWithNavController(navController)

        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (destination.id == R.id.notebookListFragment || destination.id == R.id.todolistListFragment) {

//                binding.bottomNav.startAnimation(slideUp)

                binding.bottomNav.visibility = View.VISIBLE

            } else {

//                binding.bottomNav.startAnimation(slideDown)

                binding.bottomNav.visibility = View.GONE

            }
        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = 0
            }
        }

    }
}