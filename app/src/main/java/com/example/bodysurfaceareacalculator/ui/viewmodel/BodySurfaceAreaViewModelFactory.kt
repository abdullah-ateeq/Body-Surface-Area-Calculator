package com.example.bodysurfaceareacalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bodysurfaceareacalculator.data.preferences.UserPreferencesRepository
import com.example.bodysurfaceareacalculator.util.FeedbackHelper

class BodySurfaceAreaViewModelFactory(
    private val preferencesRepository: UserPreferencesRepository,
    private val feedbackHelper: FeedbackHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BodySurfaceAreaViewModel::class.java)) {
            return BodySurfaceAreaViewModel(preferencesRepository, feedbackHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
