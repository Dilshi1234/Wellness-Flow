package com.wellnessflow.habbittracker

// Import statements for Android framework components
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

/**
 * MainActivity - The main entry point of the WellnessFlow habit tracker application
 * This class extends AppCompatActivity and handles the main navigation structure
 * It sets up the drawer navigation, toolbar, and fragment navigation
 */
class MainActivity : AppCompatActivity() {
    
    // Private properties for navigation components
    private lateinit var drawerLayout: DrawerLayout  // Main drawer layout container
    private lateinit var navigationView: NavigationView  // Navigation drawer view
    private lateinit var appBarConfiguration: AppBarConfiguration  // Configuration for app bar behavior
    
    /**
     * onCreate - Called when the activity is first created
     * This is the main initialization method that sets up the UI and navigation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display for modern Android design
        enableEdgeToEdge()
        // Set the main layout file for this activity
        setContentView(R.layout.activity_main)
        
        // Initialize window insets handling for proper spacing
        setupWindowInsets()
        // Initialize navigation system with drawer and fragments
        setupNavigation()
    }
    
    /**
     * setupWindowInsets - Configures window insets to handle system UI properly
     * This ensures the app content doesn't overlap with status bar or navigation bar
     */
    private fun setupWindowInsets() {
        // Set up window insets listener to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Get system bar insets (status bar, navigation bar)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding to main view to avoid overlap with system bars
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    /**
     * setupNavigation - Initializes the navigation system including drawer and fragments
     * This method sets up the toolbar, drawer layout, and navigation controller
     */
    private fun setupNavigation() {
        // Setup toolbar as action bar for consistent navigation
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // Initialize drawer layout and navigation view components
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        
        // Get NavHostFragment and NavController for fragment navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // Setup AppBarConfiguration with drawer - defines which fragments show drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,           // Main dashboard screen
                R.id.habitsFragment,              // Daily habits management
                R.id.statisticsFragment,          // Analytics and statistics
                R.id.calendarTimerFragment,       // Calendar and timer features
                R.id.relaxationGameFragment,      // Relaxation game feature
                R.id.userProfileFragment,         // User profile management
                R.id.hydrationReminderFragment,   // Hydration reminder system
                R.id.settingsFragment,           // App settings
                R.id.aboutFragment               // About app information
            ),
            drawerLayout  // Pass drawer layout for hamburger menu functionality
        )
        
        // Setup action bar with navigation controller for back button handling
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        // Setup navigation view with nav controller for drawer menu functionality
        navigationView.setupWithNavController(navController)
    }
    
    /**
     * onSupportNavigateUp - Handles back navigation and drawer toggle
     * This method is called when the back button or hamburger menu is pressed
     * @return Boolean - true if navigation was handled, false otherwise
     */
    override fun onSupportNavigateUp(): Boolean {
        // Get navigation controller for current fragment
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        // Navigate up using app bar configuration or fall back to default behavior
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    /**
     * onConfigurationChanged - Handles device orientation changes
     * This method is called when the device is rotated or configuration changes
     * @param newConfig - The new configuration object
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Handle orientation changes by adjusting UI accordingly
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                // Adjust UI for landscape mode - optimize layout for wider screen
                adjustUIForLandscape()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                // Adjust UI for portrait mode - optimize layout for taller screen
                adjustUIForPortrait()
            }
        }
    }
    
    /**
     * adjustUIForLandscape - Adjusts UI elements for landscape orientation
     * This method can be used to modify layouts, hide/show elements, etc.
     */
    private fun adjustUIForLandscape() {
        // Adjust UI for landscape mode
        // Currently empty - can be implemented for specific landscape optimizations
    }
    
    /**
     * adjustUIForPortrait - Adjusts UI elements for portrait orientation
     * This method can be used to modify layouts, hide/show elements, etc.
     */
    private fun adjustUIForPortrait() {
        // Adjust UI for portrait mode
        // Currently empty - can be implemented for specific portrait optimizations
    }
    
    /**
     * onResume - Called when the activity resumes from background
     * This method updates widgets and refreshes data when app becomes active
     */
    override fun onResume() {
        super.onResume()
        // Update widget when app resumes to ensure data is current
        updateWidget()
    }
    
    /**
     * updateWidget - Updates the home screen widget with current data
     * This method refreshes the widget display when the app resumes
     */
    private fun updateWidget() {
        // Get widget manager instance for updating widgets
        val widgetManager = android.appwidget.AppWidgetManager.getInstance(this)
        // Get all widget IDs for our WellnessWidgetProvider
        val widgetIds = widgetManager.getAppWidgetIds(
            android.content.ComponentName(this, com.wellnessflow.habbittracker.widget.WellnessWidgetProvider::class.java)
        )
        
        // Update widget if any widgets are installed
        if (widgetIds.isNotEmpty()) {
            // Update the first widget with current data
            com.wellnessflow.habbittracker.widget.WellnessWidgetProvider.updateAppWidget(
                this, widgetManager, widgetIds[0]
            )
        }
    }
}