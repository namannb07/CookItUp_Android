package com.naman.cookitup.repository

import com.naman.cookitup.data.Recipe
import com.naman.cookitup.data.RecipeDao
import com.naman.cookitup.data.Step
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {
    
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    
    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)
    
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    suspend fun deleteRecipe(id: Long) {
        recipeDao.deleteRecipe(id)
    }
    
    suspend fun getStepsByRecipeId(recipeId: Long): List<Step> = recipeDao.getStepsByRecipeId(recipeId)
    
    fun getStepsByRecipeIdFlow(recipeId: Long): Flow<List<Step>> = recipeDao.getStepsByRecipeIdFlow(recipeId)
    
    suspend fun insertStep(step: Step): Long = recipeDao.insertStep(step)
    
    suspend fun insertSteps(steps: List<Step>) = recipeDao.insertSteps(steps)
    
}
