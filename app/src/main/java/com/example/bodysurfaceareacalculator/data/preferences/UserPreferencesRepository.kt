package com.example.bodysurfaceareacalculator.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bodysurfaceareacalculator.data.model.AppSettings
import com.example.bodysurfaceareacalculator.data.model.AppThemeMode
import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.CalculationHistoryItem
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.domain.BsaConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bsa_calculator_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val DEFAULT_HEIGHT_UNIT = stringPreferencesKey("default_height_unit")
        val DEFAULT_WEIGHT_UNIT = stringPreferencesKey("default_weight_unit")
        val DEFAULT_FORMULA = stringPreferencesKey("default_formula")
        val SHOW_FORMULA_COMPARISON = booleanPreferencesKey("show_formula_comparison")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val CALCULATION_HISTORY = stringPreferencesKey("calculation_history_json")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeString = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
            val themeMode = try {
                AppThemeMode.valueOf(themeModeString)
            } catch (_: Exception) {
                AppThemeMode.SYSTEM
            }

            val dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: true

            val heightUnitString = preferences[PreferencesKeys.DEFAULT_HEIGHT_UNIT] ?: HeightUnit.CENTIMETERS.name
            val defaultHeightUnit = try {
                HeightUnit.valueOf(heightUnitString)
            } catch (_: Exception) {
                HeightUnit.CENTIMETERS
            }

            val weightUnitString = preferences[PreferencesKeys.DEFAULT_WEIGHT_UNIT] ?: WeightUnit.KILOGRAMS.name
            val defaultWeightUnit = try {
                WeightUnit.valueOf(weightUnitString)
            } catch (_: Exception) {
                WeightUnit.KILOGRAMS
            }

            val formulaString = preferences[PreferencesKeys.DEFAULT_FORMULA] ?: BsaFormula.MOSTELLER.name
            val defaultFormula = try {
                BsaFormula.valueOf(formulaString)
            } catch (_: Exception) {
                BsaFormula.MOSTELLER
            }

            val showFormulaComparison = preferences[PreferencesKeys.SHOW_FORMULA_COMPARISON] ?: false
            val hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true
            val soundEffects = preferences[PreferencesKeys.SOUND_EFFECTS] ?: false

            AppSettings(
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                defaultHeightUnit = defaultHeightUnit,
                defaultWeightUnit = defaultWeightUnit,
                defaultFormula = defaultFormula,
                showFormulaComparison = showFormulaComparison,
                hapticFeedback = hapticFeedback,
                soundEffects = soundEffects
            )
        }

    val calculationHistoryFlow: Flow<List<CalculationHistoryItem>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val json = preferences[PreferencesKeys.CALCULATION_HISTORY]
            CalculationHistoryJsonSerializer.deserialize(json)
        }

    suspend fun updateThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateDefaultHeightUnit(unit: HeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_HEIGHT_UNIT] = unit.name
        }
    }

    suspend fun updateDefaultWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_WEIGHT_UNIT] = unit.name
        }
    }

    suspend fun updateDefaultFormula(formula: BsaFormula) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_FORMULA] = formula.name
        }
    }

    suspend fun updateShowFormulaComparison(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_FORMULA_COMPARISON] = enabled
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun updateSoundEffects(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = AppThemeMode.SYSTEM.name
            preferences[PreferencesKeys.DYNAMIC_COLOR] = true
            preferences[PreferencesKeys.DEFAULT_HEIGHT_UNIT] = HeightUnit.CENTIMETERS.name
            preferences[PreferencesKeys.DEFAULT_WEIGHT_UNIT] = WeightUnit.KILOGRAMS.name
            preferences[PreferencesKeys.DEFAULT_FORMULA] = BsaFormula.MOSTELLER.name
            preferences[PreferencesKeys.SHOW_FORMULA_COMPARISON] = false
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = true
            preferences[PreferencesKeys.SOUND_EFFECTS] = false
        }
    }

    suspend fun saveCalculation(item: CalculationHistoryItem) {
        context.dataStore.edit { preferences ->
            val existingJson = preferences[PreferencesKeys.CALCULATION_HISTORY]
            val currentList = CalculationHistoryJsonSerializer.deserialize(existingJson).toMutableList()

            // Prepend new item to the front (newest first)
            currentList.add(0, item)

            // Keep maximum 50 records; when record #51 is added, remove the oldest
            val trimmedList = if (currentList.size > BsaConstants.MAX_HISTORY_ITEMS) {
                currentList.take(BsaConstants.MAX_HISTORY_ITEMS)
            } else {
                currentList
            }

            preferences[PreferencesKeys.CALCULATION_HISTORY] =
                CalculationHistoryJsonSerializer.serialize(trimmedList)
        }
    }

    suspend fun deleteCalculation(id: String) {
        context.dataStore.edit { preferences ->
            val existingJson = preferences[PreferencesKeys.CALCULATION_HISTORY]
            val currentList = CalculationHistoryJsonSerializer.deserialize(existingJson)
            val updatedList = currentList.filterNot { it.id == id }
            preferences[PreferencesKeys.CALCULATION_HISTORY] =
                CalculationHistoryJsonSerializer.serialize(updatedList)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.CALCULATION_HISTORY)
        }
    }
}
