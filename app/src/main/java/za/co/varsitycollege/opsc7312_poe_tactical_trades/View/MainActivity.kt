package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var navController: NavController
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var headerTitle: TextView
    private lateinit var settingsButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load language preference
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("selected_language", "English") // Default to English
        changeLanguage(selectedLanguage!!)
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        settingsButton = findViewById(R.id.ImgBtnSettings)

        firebaseAuth = FirebaseAuth.getInstance()

        setupAuthStateListener()
        //setupNavigation()
        applyTheme()

        settingsButton.setOnClickListener {
            navController.navigate(R.id.navigation_settings)
        }

        findViewById<ImageButton>(R.id.BtnBack).setOnClickListener {
            onBackPressed()
        }

    }

    private fun setAppLocale(language: String) {
        val locale = when (language) {
            "Afrikaans" -> Locale("af")
            "English" -> Locale("en")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun changeLanguage(language: String) {
        val locale = when (language) {
            "Afrikaans" -> Locale("af")
            "English" -> Locale("en")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the language preference
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", language)
            apply()
        }
    }
    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }


    fun setHeaderTitle(title: String) {
        if (::headerTitle.isInitialized) {
            headerTitle.text = title
        } else {
            Log.e("MainActivity", "headerTitleTextView is not initialized.")
        }
    }


    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.nav_host_fragment_activity_main) {
            super.onBackPressed()
        } else {
            if (navController.currentDestination?.id != R.id.navigation_home) {
                navController.popBackStack()
            }
            else
            {
                super.onBackPressed()
            }
        }
    }

    private fun setupAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {

                headerTitle = findViewById(R.id.headerTitle)
                headerTitle.text = "Home"

                setupNavigation()
            } else {
                // User is signed out
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun applyTheme() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            FirebaseHelper.getTheme(currentUser.uid) { theme, error ->
                if (error != null) {
                    Log.e("AuthStateListener", "Error retrieving theme: $error")
                } else if (theme != null) {
                    when (theme) {
                        "Dark Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        "Light Theme" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun setupNavigation() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news , R.id.navigation_wallets,R.id.navigation_home, R.id.navigation_watchlist,R.id.navigation_marketplace

            )
        )
        navView.setupWithNavController(navController)

    }
}
