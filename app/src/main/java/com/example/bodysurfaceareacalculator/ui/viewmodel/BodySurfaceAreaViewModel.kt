package com.example.bodysurfaceareacalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bodysurfaceareacalculator.data.model.AppSettings
import com.example.bodysurfaceareacalculator.data.model.AppThemeMode
import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.BsaResult
import com.example.bodysurfaceareacalculator.data.model.CalculationHistoryItem
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.data.preferences.UserPreferencesRepository
import com.example.bodysurfaceareacalculator.domain.BsaCalculator
import com.example.bodysurfaceareacalculator.domain.BsaValidator
import com.example.bodysurfaceareacalculator.domain.ValidationResult
import com.example.bodysurfaceareacalculator.util.FeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculatorUiState(
    val heightUnit: HeightUnit = HeightUnit.CENTIMETERS,
    val heightCmInput: String = "",
    val heightFeetInput: String = "",
    val heightInchesInput: String = "",
    val heightError: String? = null,

    val weightUnit: WeightUnit = WeightUnit.KILOGRAMS,
    val weightInput: String = "",
    val weightError: String? = null,

    val formula: BsaFormula = BsaFormula.MOSTELLER,
    val liveResult: BsaResult? = null,
    val activeResult: BsaResult? = null,
    val isSavedInCurrentSession: Boolean = false,
    val selectedHistoryDetailItem: CalculationHistoryItem? = null
)

class BodySurfaceAreaViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val feedbackHelper: FeedbackHelper
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = preferencesRepository.appSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val calculationHistory: StateFlow<List<CalculationHistoryItem>> = preferencesRepository.calculationHistoryFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var initializedWithDefaults = false

    init {
        viewModelScope.launch {
            appSettings.collect { settings ->
                if (!initializedWithDefaults) {
                    _uiState.update { current ->
                        current.copy(
                            heightUnit = settings.defaultHeightUnit,
                            weightUnit = settings.defaultWeightUnit,
                            formula = settings.defaultFormula
                        )
                    }
                    initializedWithDefaults = true
                }
            }
        }
    }

    fun onHeightUnitChanged(unit: HeightUnit) {
        _uiState.update { current ->
            current.copy(
                heightUnit = unit,
                heightError = null
            )
        }
        recalculateLive()
    }

    fun onHeightCmInputChanged(input: String) {
        // Allow numeric and decimal point only
        if (input.count { it == '.' } > 1) return
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { current ->
            current.copy(
                heightCmInput = filtered,
                heightError = null
            )
        }
        recalculateLive()
    }

    fun onHeightFeetInputChanged(input: String) {
        if (input.count { it == '.' } > 1) return
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { current ->
            current.copy(
                heightFeetInput = filtered,
                heightError = null
            )
        }
        recalculateLive()
    }

    fun onHeightInchesInputChanged(input: String) {
        if (input.count { it == '.' } > 1) return
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { current ->
            current.copy(
                heightInchesInput = filtered,
                heightError = null
            )
        }
        recalculateLive()
    }

    fun onWeightUnitChanged(unit: WeightUnit) {
        _uiState.update { current ->
            current.copy(
                weightUnit = unit,
                weightError = null
            )
        }
        recalculateLive()
    }

    fun onWeightInputChanged(input: String) {
        if (input.count { it == '.' } > 1) return
        val filtered = input.filter { it.isDigit() || it == '.' }
        _uiState.update { current ->
            current.copy(
                weightInput = filtered,
                weightError = null
            )
        }
        recalculateLive()
    }

    fun onFormulaChanged(formula: BsaFormula) {
        _uiState.update { current ->
            current.copy(formula = formula)
        }
        recalculateLive()
    }

    private fun recalculateLive() {
        val state = _uiState.value

        // Validate height silently for live preview
        val heightRes = when (state.heightUnit) {
            HeightUnit.CENTIMETERS -> BsaValidator.validateHeightCm(state.heightCmInput)
            HeightUnit.FEET_INCHES -> BsaValidator.validateHeightFeetInches(state.heightFeetInput, state.heightInchesInput)
        }

        // Validate weight silently for live preview
        val weightRes = when (state.weightUnit) {
            WeightUnit.KILOGRAMS -> BsaValidator.validateWeightKg(state.weightInput)
            WeightUnit.POUNDS -> BsaValidator.validateWeightLb(state.weightInput)
        }

        if (heightRes is ValidationResult.Success && weightRes is ValidationResult.Success) {
            val heightCm = heightRes.normalizedValue
            val weightKg = weightRes.normalizedValue
            val bsa = BsaCalculator.calculateBsa(heightCm, weightKg, state.formula)
            val comparisonBsa = BsaCalculator.calculateComparisonBsa(heightCm, weightKg, state.formula)

            _uiState.update {
                it.copy(
                    liveResult = BsaResult(
                        heightCm = heightCm,
                        weightKg = weightKg,
                        bsaM2 = bsa,
                        formula = state.formula,
                        comparisonBsaM2 = comparisonBsa
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(liveResult = null)
            }
        }
    }

    /**
     * Executes explicit calculation when Calculate button is clicked.
     * Returns true if successful and ready to navigate to Result screen.
     */
    fun calculateExplicit(): Boolean {
        val state = _uiState.value

        val heightRes = when (state.heightUnit) {
            HeightUnit.CENTIMETERS -> BsaValidator.validateHeightCm(state.heightCmInput)
            HeightUnit.FEET_INCHES -> BsaValidator.validateHeightFeetInches(state.heightFeetInput, state.heightInchesInput)
        }

        val weightRes = when (state.weightUnit) {
            WeightUnit.KILOGRAMS -> BsaValidator.validateWeightKg(state.weightInput)
            WeightUnit.POUNDS -> BsaValidator.validateWeightLb(state.weightInput)
        }

        var hasError = false
        var heightErrorMsg: String? = null
        var weightErrorMsg: String? = null

        if (heightRes is ValidationResult.Error) {
            hasError = true
            heightErrorMsg = heightRes.message
        }

        if (weightRes is ValidationResult.Error) {
            hasError = true
            weightErrorMsg = weightRes.message
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    heightError = heightErrorMsg,
                    weightError = weightErrorMsg
                )
            }
            if (appSettings.value.hapticFeedback) {
                feedbackHelper.triggerWarningHaptic()
            }
            return false
        }

        val heightCm = (heightRes as ValidationResult.Success).normalizedValue
        val weightKg = (weightRes as ValidationResult.Success).normalizedValue
        val bsa = BsaCalculator.calculateBsa(heightCm, weightKg, state.formula)
        val comparisonBsa = BsaCalculator.calculateComparisonBsa(heightCm, weightKg, state.formula)

        val result = BsaResult(
            heightCm = heightCm,
            weightKg = weightKg,
            bsaM2 = bsa,
            formula = state.formula,
            comparisonBsaM2 = comparisonBsa
        )

        _uiState.update {
            it.copy(
                activeResult = result,
                liveResult = result,
                heightError = null,
                weightError = null,
                isSavedInCurrentSession = false
            )
        }

        if (appSettings.value.hapticFeedback) {
            feedbackHelper.triggerLightHaptic()
        }
        if (appSettings.value.soundEffects) {
            feedbackHelper.playSubtleSound()
        }

        return true
    }

    fun resetCalculator() {
        _uiState.update {
            it.copy(
                heightUnit = HeightUnit.CENTIMETERS,
                heightCmInput = "",
                heightFeetInput = "",
                heightInchesInput = "",
                heightError = null,
                weightUnit = WeightUnit.KILOGRAMS,
                weightInput = "",
                weightError = null,
                formula = BsaFormula.MOSTELLER,
                liveResult = null,
                activeResult = null,
                isSavedInCurrentSession = false
            )
        }
        if (appSettings.value.hapticFeedback) {
            feedbackHelper.triggerLightHaptic()
        }
    }

    fun saveCalculation() {
        val state = _uiState.value
        val result = state.activeResult ?: return
        if (state.isSavedInCurrentSession) return

        val heightDisplay = when (state.heightUnit) {
            HeightUnit.CENTIMETERS -> "${state.heightCmInput.trim()} cm"
            HeightUnit.FEET_INCHES -> "${state.heightFeetInput.trim()} ft ${state.heightInchesInput.trim()} in"
        }

        val weightDisplay = when (state.weightUnit) {
            WeightUnit.KILOGRAMS -> "${state.weightInput.trim()} kg"
            WeightUnit.POUNDS -> "${state.weightInput.trim()} lb"
        }

        val dateFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())

        val historyItem = CalculationHistoryItem(
            formattedDateTime = formattedDate,
            heightDisplay = heightDisplay,
            heightUnit = state.heightUnit,
            weightDisplay = weightDisplay,
            weightUnit = state.weightUnit,
            heightCm = result.heightCm,
            weightKg = result.weightKg,
            formula = result.formula,
            bsaM2 = result.bsaM2,
            comparisonBsaM2 = result.comparisonBsaM2
        )

        viewModelScope.launch {
            preferencesRepository.saveCalculation(historyItem)
            _uiState.update { it.copy(isSavedInCurrentSession = true) }
            if (appSettings.value.hapticFeedback) {
                feedbackHelper.triggerLightHaptic()
            }
            if (appSettings.value.soundEffects) {
                feedbackHelper.playSubtleSound()
            }
        }
    }

    fun selectHistoryDetail(item: CalculationHistoryItem?) {
        _uiState.update { it.copy(selectedHistoryDetailItem = item) }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            preferencesRepository.deleteCalculation(id)
            if (appSettings.value.hapticFeedback) {
                feedbackHelper.triggerLightHaptic()
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            preferencesRepository.clearHistory()
            if (appSettings.value.hapticFeedback) {
                feedbackHelper.triggerLightHaptic()
            }
        }
    }

    // Settings actions
    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { preferencesRepository.updateThemeMode(mode) }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateDynamicColor(enabled) }
    }

    fun updateDefaultHeightUnit(unit: HeightUnit) {
        viewModelScope.launch { preferencesRepository.updateDefaultHeightUnit(unit) }
    }

    fun updateDefaultWeightUnit(unit: WeightUnit) {
        viewModelScope.launch { preferencesRepository.updateDefaultWeightUnit(unit) }
    }

    fun updateDefaultFormula(formula: BsaFormula) {
        viewModelScope.launch { preferencesRepository.updateDefaultFormula(formula) }
    }

    fun updateShowFormulaComparison(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateShowFormulaComparison(enabled) }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateHapticFeedback(enabled) }
    }

    fun updateSoundEffects(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.updateSoundEffects(enabled) }
    }

    fun resetSettings() {
        viewModelScope.launch {
            preferencesRepository.resetSettings()
            if (appSettings.value.hapticFeedback) {
                feedbackHelper.triggerLightHaptic()
            }
        }
    }
}
