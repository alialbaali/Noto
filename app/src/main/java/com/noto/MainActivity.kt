package com.noto

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.noto.database.AppDatabase
import com.noto.databinding.ActivityMainBinding
import com.noto.network.DAOs


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        DAOs.notebookDao = AppDatabase.getInstance(applicationContext).notebookDao

        DAOs.noteDao = AppDatabase.getInstance(applicationContext).noteDao

        DAOs.todolistDao = AppDatabase.getInstance(applicationContext).todolistDao

        DAOs.todoDao = AppDatabase.getInstance(applicationContext).todoDao

        DAOs.subTodoDao = AppDatabase.getInstance(applicationContext).subTodoDao

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