package com.example.bodysurfaceareacalculator.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.ui.screens.about.AboutScreen
import com.example.bodysurfaceareacalculator.ui.screens.calculator.CalculatorScreen
import com.example.bodysurfaceareacalculator.ui.screens.history.HistoryScreen
import com.example.bodysurfaceareacalculator.ui.screens.result.ResultScreen
import com.example.bodysurfaceareacalculator.ui.screens.settings.SettingsScreen
import com.example.bodysurfaceareacalculator.ui.screens.splash.SplashScreen
import com.example.bodysurfaceareacalculator.ui.viewmodel.BodySurfaceAreaViewModel

@Composable
fun BsaNavGraph(
    navController: NavHostController,
    viewModel: BodySurfaceAreaViewModel,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val historyList by viewModel.calculationHistory.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(NavRoutes.CALCULATOR) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.CALCULATOR) {
            CalculatorScreen(
                uiState = uiState,
                onHeightUnitChanged = viewModel::onHeightUnitChanged,
                onHeightCmChanged = viewModel::onHeightCmInputChanged,
                onHeightFeetChanged = viewModel::onHeightFeetInputChanged,
                onHeightInchesChanged = viewModel::onHeightInchesInputChanged,
                onWeightUnitChanged = viewModel::onWeightUnitChanged,
                onWeightChanged = viewModel::onWeightInputChanged,
                onFormulaChanged = viewModel::onFormulaChanged,
                onCalculateClicked = {
                    val success = viewModel.calculateExplicit()
                    if (success) {
                        navController.navigate(NavRoutes.RESULT)
                    }
                },
                onResetClicked = viewModel::resetCalculator
            )
        }

        composable(NavRoutes.RESULT) {
            val heightDisplay = if (uiState.heightUnit == HeightUnit.CENTIMETERS) {
                "${uiState.heightCmInput} cm"
            } else {
                "${uiState.heightFeetInput} ft ${uiState.heightInchesInput} in"
            }

            val weightDisplay = "${uiState.weightInput} ${if (uiState.weightUnit == WeightUnit.KILOGRAMS) "kg" else "lb"}"

            ResultScreen(
                result = uiState.activeResult,
                heightDisplay = heightDisplay,
                weightDisplay = weightDisplay,
                settings = settings,
                isSaved = uiState.isSavedInCurrentSession,
                onSaveClicked = viewModel::saveCalculation,
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.HISTORY) {
            HistoryScreen(
                historyList = historyList,
                selectedItem = uiState.selectedHistoryDetailItem,
                onItemSelected = viewModel::selectHistoryDetail,
                onDeleteItem = viewModel::deleteHistoryItem,
                onClearAll = viewModel::clearAllHistory
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                settings = settings,
                onThemeModeChanged = viewModel::updateThemeMode,
                onDynamicColorChanged = viewModel::updateDynamicColor,
                onDefaultHeightUnitChanged = viewModel::updateDefaultHeightUnit,
                onDefaultWeightUnitChanged = viewModel::updateDefaultWeightUnit,
                onDefaultFormulaChanged = viewModel::updateDefaultFormula,
                onShowFormulaComparisonChanged = viewModel::updateShowFormulaComparison,
                onHapticFeedbackChanged = viewModel::updateHapticFeedback,
                onSoundEffectsChanged = viewModel::updateSoundEffects,
                onResetSettings = viewModel::resetSettings,
                onClearHistory = viewModel::clearAllHistory
            )
        }

        composable(NavRoutes.ABOUT) {
            AboutScreen()
        }
    }
}
