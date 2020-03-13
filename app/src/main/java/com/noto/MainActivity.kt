package com.noto

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
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

        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNav.setupWithNavController(navController)

        var isVisible = false

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when (destination.id) {
                R.id.notebookListFragment -> {

                    binding.exFab.visibility = View.VISIBLE

                    binding.exFab.backgroundTintList = ColorStateList.valueOf(Color.BLACK)

                    binding.exFabNewNote.backgroundTintList = ColorStateList.valueOf(Color.BLACK)

                    binding.exFabNewNotebook.backgroundTintList =
                        ColorStateList.valueOf(Color.BLACK)

                    binding.bottomNav.visibility = View.VISIBLE

                    binding.root.setOnClickListener {

                        binding.exFabNewNotebook.visibility = View.GONE

                        binding.exFabNewNote.visibility = View.GONE

                        binding.exFab.text = resources.getString(R.string.create)

                        isVisible = false
                    }

                    binding.exFab.setOnClickListener {

                        if (isVisible) {

                            binding.exFabNewNotebook.visibility = View.GONE

                            binding.exFabNewNote.visibility = View.GONE

                            binding.exFab.text = resources.getString(R.string.create)

                            isVisible = false

                        } else {

                            binding.exFabNewNotebook.visibility = View.VISIBLE

                            binding.exFabNewNote.visibility = View.VISIBLE

                            binding.exFab.text = resources.getString(R.string.cancel)

                            isVisible = true

                        }
                    }
                }
                R.id.notebookFragment -> {

                    binding.exFab.visibility = View.VISIBLE

                    binding.bottomNav.visibility = View.VISIBLE

                    binding.root.setOnClickListener {

                        binding.exFabNewNote.visibility = View.GONE

                        binding.exFab.text = resources.getString(R.string.create)

                        isVisible = false
                    }

                    binding.exFab.setOnClickListener {

                        if (isVisible) {

                            binding.exFabNewNotebook.visibility = View.GONE

                            binding.exFabNewNote.visibility = View.GONE

                            binding.exFab.text = resources.getString(R.string.create)

                            isVisible = false
                        } else {

                            binding.exFabNewNote.visibility = View.VISIBLE

                            binding.exFab.text = resources.getString(R.string.cancel)

                            isVisible = true

                        }
                    }
                }
                R.id.noteFragment -> {

                    binding.bottomNav.visibility = View.GONE

                    binding.exFabNewNotebook.visibility = View.GONE

                    binding.exFabNewNote.visibility = View.GONE

                    binding.exFab.visibility = View.GONE

                    isVisible = false
                }
            }
        }
    }
}
