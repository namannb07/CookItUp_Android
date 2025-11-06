# Quick Reference: Database Connectivity Files

## 📁 Files to Show Your Teacher

### 1. **Recipe.kt** 
**Location:** `app/src/main/java/com/naman/cookitup/data/Recipe.kt`
- **What it does:** Defines the `recipes` table
- **Key annotation:** `@Entity(tableName = "recipes")`
- **Shows:** Table structure with primary key

### 2. **Step.kt**
**Location:** `app/src/main/java/com/naman/cookitup/data/Step.kt`
- **What it does:** Defines the `steps` table
- **Key annotation:** `@Entity` with `@ForeignKey`
- **Shows:** Foreign key relationship with Recipe table

### 3. **RecipeDao.kt**
**Location:** `app/src/main/java/com/naman/cookitup/data/RecipeDao.kt`
- **What it does:** Defines all database operations (SQL queries)
- **Key annotations:** `@Dao`, `@Query`, `@Insert`
- **Shows:** CRUD operations (Create, Read, Update, Delete)

### 4. **RecipeDatabase.kt**
**Location:** `app/src/main/java/com/naman/cookitup/data/RecipeDatabase.kt`
- **What it does:** Creates and manages the database
- **Key annotation:** `@Database(entities = [...], version = 2)`
- **Shows:** Database initialization using Singleton pattern

### 5. **RecipeRepository.kt**
**Location:** `app/src/main/java/com/naman/cookitup/repository/RecipeRepository.kt`
- **What it does:** Abstraction layer between ViewModels and Database
- **Shows:** Repository pattern implementation

### 6. **MainActivity.kt** (Lines 37-38)
**Location:** `app/src/main/java/com/naman/cookitup/MainActivity.kt`
- **What it does:** Initializes database connection
- **Shows:** Where database is created and connected

---

## 🎯 Key Points to Explain

1. **Room Database** - Android's SQLite abstraction layer
2. **3-Layer Architecture:**
   - Entity (Data Models)
   - DAO (Data Access Object)
   - Database (Database Manager)
3. **Repository Pattern** - Adds abstraction layer
4. **Singleton Pattern** - One database instance for entire app
5. **Foreign Keys** - Relationship between Recipe and Step tables
6. **Flow Support** - Reactive data (UI updates automatically)

---

## 💬 Sample Explanation Script

> "Our app uses Room Database for local storage. We have 5 main files:
> 
> 1. **Recipe.kt** and **Step.kt** define our database tables using `@Entity` annotations.
> 
> 2. **RecipeDao.kt** contains all our database operations like insert, delete, and query using `@Query` annotations.
> 
> 3. **RecipeDatabase.kt** creates the database instance using Room's builder. It uses a Singleton pattern so we only have one database connection.
> 
> 4. **RecipeRepository.kt** provides a clean interface for our ViewModels, following the Repository pattern.
> 
> 5. In **MainActivity.kt**, we initialize the database when the app starts.
> 
> The data flows: UI → ViewModel → Repository → DAO → Room Database → SQLite"

---

## 🔍 What Each File Demonstrates

| File | Demonstrates |
|------|-------------|
| Recipe.kt | Entity definition, Primary Key |
| Step.kt | Foreign Key relationships, CASCADE delete |
| RecipeDao.kt | SQL queries, CRUD operations |
| RecipeDatabase.kt | Database creation, Singleton pattern |
| RecipeRepository.kt | Abstraction, Clean Architecture |
| MainActivity.kt | Database initialization |

---

## 📊 Database Schema Visualization

```
┌─────────────────┐         ┌─────────────────┐
│   recipes       │         │     steps       │
├─────────────────┤         ├─────────────────┤
│ id (PK)         │◄────────│ recipeId (FK)   │
│ recipeName      │         │ id (PK)         │
│ ingredients     │         │ stepNumber      │
└─────────────────┘         │ description     │
                            │ duration       │
                            └─────────────────┘
```

**Relationship:** One Recipe has Many Steps (1:N)

