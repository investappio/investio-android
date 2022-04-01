package io.invest.app.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.invest.app.DataStore
import io.invest.app.R
import io.invest.app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var navGraph: NavGraph

    @Inject
    lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        navController = binding.navHostFragment.getFragment<NavHostFragment>().navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        // Used to know when we are at a "top level" destination
        appBarConfig = AppBarConfiguration.Builder().build()

        setupNavigation()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                    if (dataStore.getApiToken().first().isNullOrBlank()) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(R.id.register_fragment)
                        }
                    }
            }
        }

        setContentView(binding.root)
    }

    private fun setupNavigation() {
        navController.graph = navGraph
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfig)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (!appBarConfig.topLevelDestinations.contains(destination.id)) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }

            if (destination.id in listOf(R.id.login_fragment, R.id.register_fragment)) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (navController.popBackStack().not()) {
            super.onBackPressed()
        }
    }

}