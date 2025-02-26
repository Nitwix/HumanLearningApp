package com.github.HumanLearning2021.HumanLearningApp.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.ProductionDatabaseName
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.presenter.AuthenticationPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * The application's entry point.
 * Contains the navigation bars as well as the HomeFragment.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authPresenter: AuthenticationPresenter

    @Inject
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    @Inject
    @ProductionDatabaseName
    lateinit var dbName: String

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var prefs: SharedPreferences
    private lateinit var dbMgt: DatabaseManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        prefs = getSharedPreferences("LOGIN", MODE_PRIVATE)

        // for testing
        val editPrefs = prefs.edit()
        editPrefs.putBoolean("isAdmin", true)
        editPrefs.putBoolean("hasLogin", true)
        editPrefs.apply()


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.datasetsOverviewFragment,
                R.id.learningDatasetSelectionFragment,
                R.id.homeFragment
            ),
            findViewById<DrawerLayout>(R.id.drawer_layout)
        )
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        val goToDsEditingButton = bottomNav?.menu?.get(1)
        navController.addOnDestinationChangedListener { _, _, _ ->
            lifecycleScope.launch {
                goToDsEditingButton?.isVisible = false
                val user = authPresenter.currentUser
                val isAdmin = if (prefs.getBoolean("hasLogin", false)) {
                    prefs.getBoolean("isAdmin", false)
                } else {
                    user?.isAdmin ?: false
                }
                goToDsEditingButton?.isVisible = isAdmin
            }
        }

        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                dbName
            )
        }
    }
}
