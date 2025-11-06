package com.naman.cookitup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naman.cookitup.data.Recipe
import com.naman.cookitup.repository.RecipeRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyRecipesViewModel(private val repository: RecipeRepository) : ViewModel() {
    
    val allRecipes: StateFlow<List<Recipe>> = repository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    suspend fun getRecipeById(id: Long): Recipe? {
        return repository.getRecipeById(id)
    }
}

