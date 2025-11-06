package com.naman.cookitup.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeName: String,
    val ingredients: String,
    val steps: String,
    val preparationTime: String
)

