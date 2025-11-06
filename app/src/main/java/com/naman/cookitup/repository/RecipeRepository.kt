package com.naman.cookitup.repository

import com.google.gson.Gson
import com.naman.cookitup.api.RetrofitClient
import com.naman.cookitup.data.GeminiApiResponse
import com.naman.cookitup.data.GeminiRequest
import com.naman.cookitup.data.GeminiResponse
import com.naman.cookitup.data.Recipe
import com.naman.cookitup.data.RecipeDao
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {
    
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    
    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)
    
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    suspend fun searchRecipe(query: String, apiKey: String): Result<GeminiResponse> {
        return try {
            val prompt = """
                Search for a recipe for: $query
                
                Respond ONLY with a valid JSON object in this exact format (no markdown, no code blocks, no extra text):
                {
                    "recipe_name": "Recipe Name Here",
                    "ingredients": "List of ingredients here",
                    "steps": "Step-by-step instructions here",
                    "preparation_time": "Time in minutes or hours"
                }
                
                If no recipe is found, respond with:
                {
                    "recipe_name": "Recipe not found",
                    "ingredients": "",
                    "steps": "",
                    "preparation_time": ""
                }
            """.trimIndent()
            
            val request = GeminiRequest(
                contents = listOf(
                    com.naman.cookitup.data.Content(
                        parts = listOf(
                            com.naman.cookitup.data.Part(text = prompt)
                        )
                    )
                )
            )
            
            val response = RetrofitClient.geminiApiService.searchRecipe(apiKey, request)
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                
                // Check if there's an error in the response body
                if (apiResponse?.error != null) {
                    val error = apiResponse.error
                    val errorMessage = "API Error: ${error.status} - ${error.message ?: "Unknown error"}"
                    return Result.failure(Exception(errorMessage))
                }
                
                if (apiResponse != null) {
                    val text = apiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (text != null) {
                        // Try to extract JSON from the response
                        val jsonText = extractJsonFromText(text)
                        val gson = Gson()
                        val geminiResponse = gson.fromJson(jsonText, GeminiResponse::class.java)
                        
                        if (geminiResponse.recipeName == "Recipe not found") {
                            Result.failure(Exception("Recipe not found"))
                        } else {
                            Result.success(geminiResponse)
                        }
                    } else {
                        Result.failure(Exception("No response text from API. Candidates: ${apiResponse.candidates?.size ?: 0}"))
                    }
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = try {
                    response.errorBody()?.string() ?: "Unknown error"
                } catch (e: Exception) {
                    "Could not read error body: ${e.message}"
                }
                
                // Try to parse error from response body if it's JSON
                val errorMessage = try {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(errorBody, com.naman.cookitup.data.GeminiApiResponse::class.java)
                    errorResponse.error?.message ?: "API request failed: ${response.code()} - ${response.message()}"
                } catch (e: Exception) {
                    "API request failed: ${response.code()} - ${response.message()}. Error: $errorBody"
                }
                
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun extractJsonFromText(text: String): String {
        // Try to find JSON object in the text
        val jsonStart = text.indexOf('{')
        val jsonEnd = text.lastIndexOf('}') + 1
        
        return if (jsonStart >= 0 && jsonEnd > jsonStart) {
            text.substring(jsonStart, jsonEnd)
        } else {
            // If no JSON found, return a "not found" response
            """{"recipe_name": "Recipe not found", "ingredients": "", "steps": "", "preparation_time": ""}"""
        }
    }
}

