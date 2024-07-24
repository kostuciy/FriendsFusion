package com.kostuciy.friendsfusion.core.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.kostuciy.friendsfusion.R
import com.kostuciy.friendsfusion.databinding.ActivityMainBinding
import com.kostuciy.friendsfusion.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration

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

        profileViewModel.setVkAuthResultLauncher(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isGone =
                destination.id == R.id.signInFragment ||
                destination.id == R.id.signUpFragment
        }

        appBarConfig =
            AppBarConfiguration(
                setOf(
                    R.id.profileFragment,
                    R.id.chatFragment,
                    R.id.eventFragment,
                    R.id.galleryFragment,
                ),
            )

        with(binding) {
            bottomNavigationView.setupWithNavController(navController)
//            setupActionBarWithNavController(navController, appBarConfig)
        }
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(appBarConfig)
}
