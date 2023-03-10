package com.example.shopiapp.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.shopiapp.R
import com.example.shopiapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationMenu.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signInFragment -> hideNavigationMenu(binding)
                else -> showNavigationMenu(binding)
            }
        }
    }
    private fun hideNavigationMenu(binding: ActivityMainBinding) {
        binding.bottomNavigationMenu.visibility = View.GONE
    }

    private fun showNavigationMenu(binding: ActivityMainBinding) {
        binding.bottomNavigationMenu.visibility = View.VISIBLE
    }
}