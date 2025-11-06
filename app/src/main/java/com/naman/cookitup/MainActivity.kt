package com.naman.cookitup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import com.naman.cookitup.ui.screens.MyRecipesScreen
import com.naman.cookitup.ui.screens.RecipeDetailScreen
import com.naman.cookitup.ui.screens.SearchRecipeScreen
import com.naman.cookitup.ui.theme.CookItUpTheme
import com.naman.cookitup.viewmodel.MyRecipesViewModel
import com.naman.cookitup.viewmodel.SearchRecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = RecipeDatabase.getDatabase(applicationContext)
        val repository = RecipeRepository(database.recipeDao())
        
        // TODO: Replace with your Gemini API key
        // You can also store this in local.properties or use BuildConfig
        val geminiApiKey = "AIzaSyADP8N8VZGJ1-F9-r0LF9GAVEpNoliLf4U"
        
        setContent {
            CookItUpTheme {
                MainScreen(repository = repository, apiKey = geminiApiKey)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: RecipeRepository, apiKey: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val searchViewModel: SearchRecipeViewModel = viewModel {
        SearchRecipeViewModel(repository)
    }
    val myRecipesViewModel: MyRecipesViewModel = viewModel {
        MyRecipesViewModel(repository)
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Search") },
                    label = { Text("Search Recipe") },
                    selected = currentDestination?.hierarchy?.any { it.route == "search" } == true,
                    onClick = {
                        navController.navigate("search") {
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
            startDestination = "search",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("search") {
                SearchRecipeScreen(
                    viewModel = searchViewModel,
                    apiKey = apiKey
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
                var isLoading by remember { mutableStateOf(true) }
                
                LaunchedEffect(recipeId) {
                    if (recipeId != null) {
                        recipe = myRecipesViewModel.getRecipeById(recipeId)
                        isLoading = false
                    } else {
                        isLoading = false
                    }
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    RecipeDetailScreen(
                        recipe = recipe,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
