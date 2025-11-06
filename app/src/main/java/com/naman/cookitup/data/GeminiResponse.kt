package com.naman.cookitup.data

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("recipe_name")
    val recipeName: String?,
    val ingredients: String?,
    val steps: String?,
    @SerializedName("preparation_time")
    val preparationTime: String?
)

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiApiResponse(
    val candidates: List<Candidate>?,
    val error: GeminiError?
)

data class Candidate(
    val content: Content
)

data class GeminiError(
    val code: Int?,
    val message: String?,
    val status: String?
)

