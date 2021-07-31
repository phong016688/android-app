package com.datn.cookingguideapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.datn.cookingguideapp.BaseActivity
import com.datn.cookingguideapp.R
import com.datn.cookingguideapp.databinding.ActivityMainBinding
import com.datn.cookingguideapp.utils.viewBindings
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class HomeActivity : BaseActivity(R.layout.activity_main) {
    private val viewBinding by viewBindings(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //changeLanguage(Locale.US)
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navView: BottomNavigationView = viewBinding.navView
        val navController = navHost.navController
        navView.setupWithNavController(navController)
    }

    @Suppress("DEPRECATION")
    private fun changeLanguage(locale: Locale) {
        if (resources.configuration.locale.displayLanguage == locale.displayLanguage) return
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        recreate()
    }

    companion object {
        fun makeActivity(from: Context): Intent {
            return Intent(from, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }
}