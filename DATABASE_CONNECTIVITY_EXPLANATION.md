# Database Connectivity Architecture - CookItUp App

## Overview
This app uses **Room Database** (Android's SQLite abstraction layer) for local data storage. The architecture follows the **Repository Pattern** for clean separation of concerns.

---

## Files Responsible for Database Connectivity

### 1. **Entity Classes** (Data Models)
These define the database tables and their structure.

#### `app/src/main/java/com/naman/cookitup/data/Recipe.kt`
- **Purpose**: Defines the `recipes` table structure
- **Annotations**:
  - `@Entity(tableName = "recipes")` - Marks as a Room entity
  - `@PrimaryKey(autoGenerate = true)` - Auto-incrementing primary key
- **Fields**:
  - `id: Long` - Primary key
  - `recipeName: String` - Recipe name
  - `ingredients: String` - Ingredients list

#### `app/src/main/java/com/naman/cookitup/data/Step.kt`
- **Purpose**: Defines the `steps` table structure
- **Annotations**:
  - `@Entity(tableName = "steps")` - Marks as a Room entity
  - `@ForeignKey` - Creates relationship with Recipe table
  - `onDelete = ForeignKey.CASCADE` - Deletes steps when recipe is deleted
- **Fields**:
  - `id: Long` - Primary key
  - `recipeId: Long` - Foreign key to recipes table
  - `stepNumber: Int` - Step order
  - `description: String` - Step description
  - `duration: String` - Step duration

---

### 2. **DAO (Data Access Object)**
Defines all database operations (CRUD - Create, Read, Update, Delete).

#### `app/src/main/java/com/naman/cookitup/data/RecipeDao.kt`
- **Purpose**: Interface that defines all database queries
- **Key Annotations**:
  - `@Dao` - Marks as a Room DAO
  - `@Query` - SQL queries for SELECT, DELETE operations
  - `@Insert` - For INSERT operations
- **Operations**:
  - `getAllRecipes()` - Returns Flow of all recipes
  - `getRecipeById(id)` - Get single recipe
  - `insertRecipe(recipe)` - Insert new recipe
  - `deleteRecipe(id)` - Delete recipe
  - `getStepsByRecipeId(recipeId)` - Get steps for a recipe
  - `insertStep(step)` / `insertSteps(steps)` - Insert steps
  - `deleteStepsByRecipeId(recipeId)` - Delete steps

---

### 3. **Database Class**
Creates and manages the database instance.

#### `app/src/main/java/com/naman/cookitup/data/RecipeDatabase.kt`
- **Purpose**: Main database class that ties everything together
- **Key Components**:
  - `@Database(entities = [Recipe::class, Step::class], version = 2)` - Defines entities and version
  - `abstract fun recipeDao(): RecipeDao` - Provides DAO access
  - **Singleton Pattern**: Uses `companion object` to ensure only one database instance
  - `getDatabase(context)` - Factory method to get database instance
  - Uses `Room.databaseBuilder()` to build the database
  - Database name: `"recipe_database"`
  - `fallbackToDestructiveMigration()` - Handles schema changes

---

### 4. **Repository Layer**
Abstracts data sources and provides clean API to ViewModels.

#### `app/src/main/java/com/naman/cookitup/repository/RecipeRepository.kt`
- **Purpose**: Single source of truth for data operations
- **Benefits**:
  - Hides database implementation details from ViewModels
  - Can easily switch data sources (local DB, remote API, etc.)
  - Provides business logic (e.g., cascading deletes)
- **Methods**:
  - Wraps all DAO methods
  - `deleteRecipe()` - Deletes steps first, then recipe (cascading delete logic)

---

### 5. **Initialization Point**
Where the database connection is established.

#### `app/src/main/java/com/naman/cookitup/MainActivity.kt` (Lines 37-38)
```kotlin
val database = RecipeDatabase.getDatabase(applicationContext)
val repository = RecipeRepository(database.recipeDao())
```
- **Purpose**: Initializes database and repository at app startup
- **Flow**: 
  1. Get database instance using application context
  2. Get DAO from database
  3. Create repository with DAO
  4. Pass repository to ViewModels

---

## Data Flow Architecture

```
MainActivity
    ↓
RecipeDatabase.getDatabase() → Creates Database Instance
    ↓
database.recipeDao() → Gets DAO Interface
    ↓
RecipeRepository(dao) → Creates Repository
    ↓
ViewModels (AddRecipeViewModel, MyRecipesViewModel, etc.)
    ↓
UI Components (Screens)
```

---

## How Database Operations Work

### **Insert Operation Example:**
1. User adds a recipe in UI
2. ViewModel calls `repository.insertRecipe(recipe)`
3. Repository calls `recipeDao.insertRecipe(recipe)`
4. Room executes SQL INSERT
5. Returns generated ID

### **Read Operation Example:**
1. UI observes `viewModel.allRecipes` (Flow)
2. ViewModel gets Flow from repository
3. Repository returns Flow from DAO
4. Room queries database and emits results
5. UI automatically updates when data changes

### **Delete Operation Example:**
1. User clicks delete button
2. ViewModel calls `repository.deleteRecipe(id)`
3. Repository first deletes steps: `recipeDao.deleteStepsByRecipeId(id)`
4. Then deletes recipe: `recipeDao.deleteRecipe(id)`
5. Room executes SQL DELETE statements

---

## Key Room Database Features Used

1. **Flow Support**: Reactive data updates (UI updates automatically)
2. **Coroutines**: All operations are suspend functions (async)
3. **Foreign Keys**: Relationship between Recipe and Step tables
4. **Cascade Delete**: Steps deleted when recipe is deleted
5. **Type Converters**: Not needed here (using simple types)

---

## Database Schema

### Table: `recipes`
| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY, AUTO_INCREMENT |
| recipeName | TEXT | NOT NULL |
| ingredients | TEXT | |

### Table: `steps`
| Column | Type | Constraints |
|--------|------|-------------|
| id | INTEGER | PRIMARY KEY, AUTO_INCREMENT |
| recipeId | INTEGER | FOREIGN KEY → recipes.id |
| stepNumber | INTEGER | |
| description | TEXT | |
| duration | TEXT | |

---

## Dependencies Required

### `app/build.gradle.kts` (Lines 55-57)
```kotlin
// Room
implementation(libs.androidx.room.runtime)    // Room runtime library
implementation(libs.androidx.room.ktx)        // Kotlin extensions (Flow, Coroutines)
kapt(libs.androidx.room.compiler)             // Annotation processor for code generation
```

**Version:** Room 2.6.1 (from `gradle/libs.versions.toml`)

**What each does:**
- `room-runtime`: Core Room functionality
- `room-ktx`: Kotlin extensions for Flow and Coroutines support
- `room-compiler`: Generates implementation code from annotations at compile time

---

## Summary for Presentation

### **5 Main Files for Database Connectivity:**

1. **Recipe.kt** - Recipe table structure (Entity)
2. **Step.kt** - Steps table structure (Entity with Foreign Key)
3. **RecipeDao.kt** - Database operations interface (Queries)
4. **RecipeDatabase.kt** - Database creation and management (Singleton)
5. **RecipeRepository.kt** - Abstraction layer for data access (Business Logic)

### **Initialization Point:**
- `MainActivity.kt` (lines 37-38) - Where database connection is established

### **Architecture Pattern:** 
Repository Pattern with Room Database

### **Database Type:** 
SQLite (via Room abstraction)

### **Key Benefits:**
- ✅ Type-safe queries (compile-time checking)
- ✅ Automatic schema management
- ✅ Reactive data with Flow (automatic UI updates)
- ✅ Thread-safe operations (Room handles threading)
- ✅ Relationship management (Foreign Keys with CASCADE)
- ✅ No raw SQL errors (Room validates queries)

### **Data Flow:**
```
UI → ViewModel → Repository → DAO → Room Database → SQLite
```

