package com.example.arthub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.arthub.navigation.MyBottomNavigationBar
import com.example.arthub.navigation.MyTopAppBar
import com.example.arthub.navigation.Navigation
import com.example.arthub.screen.Screen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            MyApp()
        }

    }
}


@Composable
fun MyApp() {
    val currentScreen = remember { mutableStateOf<Screen>(Screen.PostsScreen) }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "ArtHub",
                onSearchClicked = {
                    // Handle search click
                },
                onMoreClicked = {
                    // Handle more options click
                }
            )
        },
        bottomBar = {
            MyBottomNavigationBar(
                items = listOf("Posts", "Favorites", "Profile"),
                currentRoute = currentScreen.value,
                onItemSelected = { selectedItem ->
                    currentScreen.value = when (selectedItem) {
                        "Posts" -> Screen.PostsScreen
//                        "Favorites" -> Screen.FavoritesScreen // Ensure this screen exists
//                        "Profile" -> Screen.ProfileScreen // Ensure this screen exists
                        else -> Screen.PostsScreen // Default to PostsScreen
                    }
                }
            )
        }
    ) { paddingValues -> // This block provides padding values
        Navigation(
            currentScreen = currentScreen.value,
            onNavigateToPostDetail = { postId ->
                currentScreen.value = Screen.PostDetail(postId)
            },
            onNavigateBack = {
                currentScreen.value = Screen.PostsScreen
            },
            modifier = Modifier.padding(paddingValues) // Ensure to use padding here
        )
    }
}




