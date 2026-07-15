package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.Repository
import com.example.ui.AuthState
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SigmaOrange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                // Initialize Room Database and Repository
                val database = remember { AppDatabase.getDatabase(applicationContext) }
                val repository = remember { Repository(database.appDao()) }
                
                // Create ViewModel with simple Factory Injection
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                val authState by viewModel.authState.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (authState) {
                        is AuthState.Authenticated -> {
                            // Main Application Flow with Bottom Navigation
                            MainAppContent(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        else -> {
                            // Login, Registration, & Forgot Password flow
                            AuthScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        NavigationTab("Home", Icons.Default.Home, "dashboard_tab"),
        NavigationTab("Latihan", Icons.Default.FitnessCenter, "workout_tab"),
        NavigationTab("Kalkulator", Icons.Default.Calculate, "calc_tab"),
        NavigationTab("Nutrisi", Icons.Default.Restaurant, "nutrition_tab"),
        NavigationTab("Progres", Icons.Default.BarChart, "progress_tab"),
        NavigationTab("AI Coach", Icons.Default.AutoAwesome, "ai_tab")
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding() // prevent system bar overlapping as mandated
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SigmaOrange,
                            selectedTextColor = SigmaOrange,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Animate transition when switching tabs
            AnimatedVisibility(
                visible = selectedTab == 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { targetTab -> selectedTab = targetTab }
                )
            }

            AnimatedVisibility(
                visible = selectedTab == 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                WorkoutScreen(viewModel = viewModel)
            }

            AnimatedVisibility(
                visible = selectedTab == 2,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CalculatorsScreen(viewModel = viewModel)
            }

            AnimatedVisibility(
                visible = selectedTab == 3,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NutritionScreen(viewModel = viewModel)
            }

            AnimatedVisibility(
                visible = selectedTab == 4,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ProgressPhotoScreen(viewModel = viewModel)
            }

            AnimatedVisibility(
                visible = selectedTab == 5,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AiCoachScreen(viewModel = viewModel)
            }
        }
    }
}

private data class NavigationTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
