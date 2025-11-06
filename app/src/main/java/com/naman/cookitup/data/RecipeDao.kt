package com.naman.cookitup.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipe(id: Long)
    
    // Step operations
    @Query("SELECT * FROM steps WHERE recipeId = :recipeId ORDER BY stepNumber ASC")
    suspend fun getStepsByRecipeId(recipeId: Long): List<Step>
    
    @Query("SELECT * FROM steps WHERE recipeId = :recipeId ORDER BY stepNumber ASC")
    fun getStepsByRecipeIdFlow(recipeId: Long): Flow<List<Step>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<Step>)
    
    @Query("DELETE FROM steps WHERE recipeId = :recipeId")
    suspend fun deleteStepsByRecipeId(recipeId: Long)
    
}

