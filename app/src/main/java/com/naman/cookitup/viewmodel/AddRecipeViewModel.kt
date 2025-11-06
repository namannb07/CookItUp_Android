package com.naman.cookitup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naman.cookitup.data.Recipe
import com.naman.cookitup.data.Step
import com.naman.cookitup.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddRecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    
    private val _recipeName = MutableStateFlow("")
    val recipeName: StateFlow<String> = _recipeName
    
    private val _ingredients = MutableStateFlow("")
    val ingredients: StateFlow<String> = _ingredients
    
    private val _steps = MutableStateFlow<List<StepData>>(emptyList())
    val steps: StateFlow<List<StepData>> = _steps
    
    private val _currentStepDescription = MutableStateFlow("")
    val currentStepDescription: StateFlow<String> = _currentStepDescription
    
    private val _currentStepDuration = MutableStateFlow("")
    val currentStepDuration: StateFlow<String> = _currentStepDuration
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving
    
    data class StepData(
        val stepNumber: Int,
        val description: String,
        val duration: String
    )
    
    fun updateRecipeName(name: String) {
        _recipeName.value = name
    }
    
    fun updateIngredients(ingredients: String) {
        _ingredients.value = ingredients
    }
    
    fun updateCurrentStepDescription(description: String) {
        _currentStepDescription.value = description
    }
    
    fun updateCurrentStepDuration(duration: String) {
        _currentStepDuration.value = duration
    }
    
    fun addStep() {
        val description = _currentStepDescription.value.trim()
        val duration = _currentStepDuration.value.trim()
        
        if (description.isNotBlank() && duration.isNotBlank()) {
            val newStep = StepData(
                stepNumber = _steps.value.size + 1,
                description = description,
                duration = duration
            )
            _steps.value = _steps.value + newStep
            _currentStepDescription.value = ""
            _currentStepDuration.value = ""
        }
    }
    
    fun removeStep(stepNumber: Int) {
        _steps.value = _steps.value
            .filter { it.stepNumber != stepNumber }
            .mapIndexed { index, step -> step.copy(stepNumber = index + 1) }
    }
    
    suspend fun saveRecipe(): Boolean {
        val name = _recipeName.value.trim()
        val ingredients = _ingredients.value.trim()
        
        if (name.isBlank() || _steps.value.isEmpty()) {
            return false
        }
        
        _isSaving.value = true
        
        return try {
            // Save recipe
            val recipe = Recipe(
                recipeName = name,
                ingredients = ingredients
            )
            val recipeId = repository.insertRecipe(recipe)
            
            // Save steps
            val steps = _steps.value.map { stepData ->
                Step(
                    recipeId = recipeId,
                    stepNumber = stepData.stepNumber,
                    description = stepData.description,
                    duration = stepData.duration
                )
            }
            repository.insertSteps(steps)
            
            // Reset form
            _recipeName.value = ""
            _ingredients.value = ""
            _steps.value = emptyList()
            _currentStepDescription.value = ""
            _currentStepDuration.value = ""
            
            true
        } catch (e: Exception) {
            false
        } finally {
            _isSaving.value = false
        }
    }
}

