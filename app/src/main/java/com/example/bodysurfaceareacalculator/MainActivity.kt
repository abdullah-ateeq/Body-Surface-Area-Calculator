package com.example.bodysurfaceareacalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bodysurfaceareacalculator.data.preferences.UserPreferencesRepository
import com.example.bodysurfaceareacalculator.ui.navigation.BsaBottomBar
import com.example.bodysurfaceareacalculator.ui.navigation.BsaNavGraph
import com.example.bodysurfaceareacalculator.ui.navigation.NavRoutes
import com.example.bodysurfaceareacalculator.ui.theme.BodySurfaceAreaCalculatorTheme
import com.example.bodysurfaceareacalculator.ui.viewmodel.BodySurfaceAreaViewModel
import com.example.bodysurfaceareacalculator.ui.viewmodel.BodySurfaceAreaViewModelFactory
import com.example.bodysurfaceareacalculator.util.FeedbackHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository = UserPreferencesRepository(applicationContext)
        val feedbackHelper = FeedbackHelper(applicationContext)
        val viewModelFactory = BodySurfaceAreaViewModelFactory(preferencesRepository, feedbackHelper)

        setContent {
            val viewModel: BodySurfaceAreaViewModel = viewModel(factory = viewModelFactory)
            val settings by viewModel.appSettings.collectAsState()
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Splash and Result screens must not show bottom navigation
            val showBottomBar = currentRoute != null &&
                    currentRoute != NavRoutes.SPLASH &&
                    currentRoute != NavRoutes.RESULT

            BodySurfaceAreaCalculatorTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BsaBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(NavRoutes.CALCULATOR) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    BsaNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}