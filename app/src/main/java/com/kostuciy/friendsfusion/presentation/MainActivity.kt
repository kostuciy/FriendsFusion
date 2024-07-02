package com.kostuciy.friendsfusion.presentation

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isGone =
                destination.id == R.id.signInFragment || destination.id == R.id.signUpFragment
        }

        with(binding) {
            bottomNavigationView.setOnItemSelectedListener { item ->
                navController.navigate(
                    when (item.itemId) {
                        R.id.profile -> R.id.profileFragment
                        R.id.chat -> R.id.chatFragment
                        R.id.gallery -> R.id.galleryFragment
                        R.id.events -> R.id.eventFragment
                        else -> return@setOnItemSelectedListener false
                    }
                )
                true
            }

            onBackPressedDispatcher.addCallback(this@MainActivity) {
                navController.previousBackStackEntry?.destination?.id?.let {
                    bottomNavigationView.selectedItemId = when (it) {
                        R.id.profileFragment -> R.id.profile
                        R.id.chatFragment -> R.id.chat
                        R.id.galleryFragment -> R.id.gallery
                        R.id.eventFragment -> R.id.events
                        else -> return@addCallback
                    }
                }
            }
        }
    }
}