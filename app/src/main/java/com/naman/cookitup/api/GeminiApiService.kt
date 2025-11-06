package com.naman.cookitup.api

import com.naman.cookitup.data.GeminiApiResponse
import com.naman.cookitup.data.GeminiRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun searchRecipe(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiApiResponse>
}
