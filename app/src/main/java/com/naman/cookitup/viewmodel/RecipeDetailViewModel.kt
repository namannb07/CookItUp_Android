package com.naman.cookitup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naman.cookitup.data.Step
import com.naman.cookitup.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(private val repository: RecipeRepository) : ViewModel() {
    
    private val _steps = MutableStateFlow<List<Step>>(emptyList())
    val steps: StateFlow<List<Step>> = _steps
    
    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex
    
    private val _isCookingMode = MutableStateFlow(false)
    val isCookingMode: StateFlow<Boolean> = _isCookingMode
    
    private val _remainingTimeSeconds = MutableStateFlow(0)
    val remainingTimeSeconds: StateFlow<Int> = _remainingTimeSeconds
    
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning
    
    fun loadSteps(recipeId: Long) {
        viewModelScope.launch {
            _steps.value = repository.getStepsByRecipeId(recipeId)
        }
    }
    
    fun startCookingMode() {
        if (_steps.value.isNotEmpty()) {
            _isCookingMode.value = true
            _currentStepIndex.value = 0
            startTimerForCurrentStep()
        }
    }
    
    fun stopCookingMode() {
        _isCookingMode.value = false
        _isTimerRunning.value = false
        _currentStepIndex.value = 0
        _remainingTimeSeconds.value = 0
    }
    
    fun getCurrentStep(): Step? {
        return _steps.value.getOrNull(_currentStepIndex.value)
    }
    
    fun moveToNextStep() {
        if (_currentStepIndex.value < _steps.value.size - 1) {
            _currentStepIndex.value++
            startTimerForCurrentStep()
        }
    }
    
    fun isLastStep(): Boolean {
        return _currentStepIndex.value >= _steps.value.size - 1
    }
    
    private fun startTimerForCurrentStep() {
        val currentStep = getCurrentStep() ?: return
        _remainingTimeSeconds.value = parseDurationToSeconds(currentStep.duration)
        _isTimerRunning.value = true
    }
    
    fun updateTimer(seconds: Int) {
        _remainingTimeSeconds.value = seconds
        if (seconds <= 0 && _isTimerRunning.value) {
            _isTimerRunning.value = false
            if (!isLastStep()) {
                moveToNextStep()
            }
        }
    }
    
    fun pauseTimer() {
        _isTimerRunning.value = false
    }
    
    fun resumeTimer() {
        if (_remainingTimeSeconds.value > 0) {
            _isTimerRunning.value = true
        }
    }
    
    private fun parseDurationToSeconds(duration: String): Int {
        val lower = duration.lowercase().trim()
        return when {
            lower.contains("minute") -> {
                val minutes = lower.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                minutes * 60
            }
            lower.contains("second") -> {
                lower.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            }
            lower.contains("hour") -> {
                val hours = lower.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                hours * 3600
            }
            else -> {
                // Try to extract number and assume minutes
                val num = lower.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                num * 60
            }
        }
    }
}

