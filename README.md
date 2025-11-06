# CookItUp - Recipe Search and Storage App

A simple Android recipe search and storage application built with Jetpack Compose, Room Database, and Gemini API.

## Features

- **Search Recipe**: Search for recipes using Gemini API
- **Save Recipes**: Save favorite recipes to local SQLite database
- **My Recipes**: View all saved recipes offline
- **Recipe Details**: View full recipe details including ingredients and steps

## Setup Instructions

### 1. Get Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key for Gemini API
3. Copy your API key

### 2. Configure API Key

Open `app/src/main/java/com/naman/cookitup/MainActivity.kt` and replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key:

```kotlin
val geminiApiKey = "YOUR_ACTUAL_API_KEY_HERE"
```

**Note**: For production apps, consider storing the API key securely using:
- `local.properties` file (add to `.gitignore`)
- Android Keystore
- BuildConfig (for development only)

### 3. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an emulator or physical device

## Project Structure

```
app/src/main/java/com/naman/cookitup/
├── data/
│   ├── Recipe.kt              # Room entity
│   ├── RecipeDao.kt           # Database access object
│   ├── RecipeDatabase.kt      # Room database
│   └── GeminiResponse.kt      # API response models
├── api/
│   ├── GeminiApiService.kt    # Retrofit API interface
│   └── RetrofitClient.kt      # Retrofit client setup
├── repository/
│   └── RecipeRepository.kt    # Repository for data operations
├── viewmodel/
│   ├── SearchRecipeViewModel.kt
│   └── MyRecipesViewModel.kt
├── ui/
│   └── screens/
│       ├── SearchRecipeScreen.kt
│       ├── MyRecipesScreen.kt
│       └── RecipeDetailScreen.kt
└── MainActivity.kt            # Main activity with navigation
```

## Technologies Used

- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local SQLite database
- **Retrofit**: HTTP client for API calls
- **Navigation Compose**: Navigation between screens
- **ViewModel**: UI-related data holder
- **Coroutines & Flow**: Asynchronous programming
- **Material 3**: Material Design components

## API Response Format

The Gemini API is configured to respond in the following JSON format:

```json
{
  "recipe_name": "Recipe Name",
  "ingredients": "List of ingredients",
  "steps": "Step-by-step instructions",
  "preparation_time": "Time in minutes or hours"
}
```

If no recipe is found, the API responds with:
```json
{
  "recipe_name": "Recipe not found",
  "ingredients": "",
  "steps": "",
  "preparation_time": ""
}
```

## Database Schema

The app uses Room database with the following schema:

- **Table**: `recipes`
- **Columns**:
  - `id`: Long (Primary Key, Auto-generated)
  - `recipeName`: String
  - `ingredients`: String
  - `steps`: String
  - `preparationTime`: String

## Permissions

The app requires the following permissions:
- `INTERNET`: To make API calls to Gemini
- `ACCESS_NETWORK_STATE`: To check network connectivity

## Notes

- All recipe data is stored locally in SQLite
- No cloud storage or online database is used
- Recipes are saved offline and accessible without internet connection
- The app requires internet only for searching new recipes

