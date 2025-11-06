package com.naman.cookitup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naman.cookitup.data.GeminiResponse
import com.naman.cookitup.data.Recipe
import com.naman.cookitup.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchRecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _searchResult = MutableStateFlow<GeminiResponse?>(null)
    val searchResult: StateFlow<GeminiResponse?> = _searchResult
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun searchRecipe(apiKey: String) {
        if (_searchQuery.value.isBlank()) {
            _errorMessage.value = "Please enter a recipe name"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _searchResult.value = null
            
            repository.searchRecipe(_searchQuery.value, apiKey)
                .onSuccess { response ->
                    _searchResult.value = response
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to search recipe"
                    _searchResult.value = null
                }
            
            _isLoading.value = false
        }
    }
    
    suspend fun saveRecipe(): Boolean {
        val result = _searchResult.value ?: return false
        
        val recipe = Recipe(
            recipeName = result.recipeName ?: "",
            ingredients = result.ingredients ?: "",
            steps = result.steps ?: "",
            preparationTime = result.preparationTime ?: ""
        )
        repository.insertRecipe(recipe)
        return true
    }
    
    suspend fun saveManualRecipe(
        recipeName: String,
        ingredients: String,
        steps: String,
        preparationTime: String
    ): Boolean {
        if (recipeName.isBlank()) {
            return false
        }
        
        val recipe = Recipe(
            recipeName = recipeName,
            ingredients = ingredients,
            steps = steps,
            preparationTime = preparationTime
        )
        repository.insertRecipe(recipe)
        return true
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSearchResult() {
        _searchResult.value = null
    }
}

