package com.noto

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.noto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when (destination.id) {
                R.id.noteFragment -> {
                    binding.bottomNav.visibility = View.GONE
                    binding.exFab.visibility = View.GONE
                }
                R.id.notebookListFragment -> {
                    binding.exFab.let { exFab ->
                        exFab.backgroundTintList =
                            ColorStateList.valueOf(Color.BLACK)
                        exFab.foregroundTintList = ColorStateList.valueOf(Color.WHITE)
                    }
                }
                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    binding.exFab.visibility = View.VISIBLE
                }
            }
        }
    }
}
