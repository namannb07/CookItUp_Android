package com.naman.cookitup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.naman.cookitup.data.RecipeDatabase
import com.naman.cookitup.repository.RecipeRepository
import com.naman.cookitup.ui.screens.AddRecipeScreen
import com.naman.cookitup.ui.screens.MyRecipesScreen
import com.naman.cookitup.ui.screens.RecipeDetailScreen
import com.naman.cookitup.ui.theme.CookItUpTheme
import com.naman.cookitup.viewmodel.AddRecipeViewModel
import com.naman.cookitup.viewmodel.MyRecipesViewModel
import com.naman.cookitup.viewmodel.RecipeDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = RecipeDatabase.getDatabase(applicationContext)
        val repository = RecipeRepository(database.recipeDao())
        
        setContent {
            CookItUpTheme {
                MainScreen(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: RecipeRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val addRecipeViewModel: AddRecipeViewModel = viewModel {
        AddRecipeViewModel(repository)
    }
    val myRecipesViewModel: MyRecipesViewModel = viewModel {
        MyRecipesViewModel(repository)
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Recipe") },
                    label = { Text("Add Recipe") },
                    selected = currentDestination?.hierarchy?.any { it.route == "add_recipe" } == true,
                    onClick = {
                        navController.navigate("add_recipe") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "My Recipes") },
                    label = { Text("My Recipes") },
                    selected = currentDestination?.hierarchy?.any { it.route == "my_recipes" } == true,
                    onClick = {
                        navController.navigate("my_recipes") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "add_recipe",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("add_recipe") {
                AddRecipeScreen(
                    viewModel = addRecipeViewModel,
                    onRecipeSaved = {
                        // Optionally navigate to My Recipes after saving
                        // navController.navigate("my_recipes")
                    }
                )
            }
            composable("my_recipes") {
                MyRecipesScreen(
                    viewModel = myRecipesViewModel,
                    onRecipeClick = { recipeId ->
                        navController.navigate("recipe_detail/$recipeId")
                    }
                )
            }
            composable("recipe_detail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")?.toLongOrNull()
                var recipe by remember { mutableStateOf<com.naman.cookitup.data.Recipe?>(null) }
                
                val recipeDetailViewModel: RecipeDetailViewModel = viewModel {
                    RecipeDetailViewModel(repository)
                }
                
                LaunchedEffect(recipeId) {
                    if (recipeId != null) {
                        recipe = myRecipesViewModel.getRecipeById(recipeId)
                        recipeDetailViewModel.loadSteps(recipeId)
                    }
                }
                
                RecipeDetailScreen(
                    recipe = recipe,
                    viewModel = recipeDetailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
