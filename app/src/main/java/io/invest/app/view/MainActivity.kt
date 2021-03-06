package io.invest.app.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.LocalStore
import io.invest.app.R
import io.invest.app.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

// Used to know when we are at a "top level" destination
val appBarConfig = AppBarConfiguration.Builder(
    R.id.portfolio_fragment,
    R.id.profile_fragment,
    R.id.browse_fragment,
    R.id.leaderboard_fragment
).build()

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var localStore: LocalStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        setupNavigation()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (localStore.getApiToken().first().isNullOrBlank()) {
                    clearBackStack()
                    navController.navigate(R.id.login_fragment)
                }
            }
        }

        setContentView(binding.root)
    }

    private fun setupNavigation() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Bottom navigation's tlds should not have back stack entries
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!appBarConfig.topLevelDestinations.contains(destination.id)) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    private fun clearBackStack() {
        navController.currentDestination?.let {
            navController.popBackStack(it.id, true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) ||
                super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onBackPressed() {
        if (navController.popBackStack().not()) {
            super.onBackPressed()
        }
    }

}