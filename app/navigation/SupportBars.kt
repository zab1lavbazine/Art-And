package com.example.arthub.navigation


import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.arthub.screen.Screen

val screenIcons = mapOf(
    "Posts" to Icons.Filled.Home,
    "Favorites" to Icons.Filled.Favorite,
    "Profile" to Icons.Filled.Person
)

@Composable
fun MyBottomNavigationBar(items: List<String>, currentRoute: Screen, onItemSelected: (String) -> Unit) {
    BottomNavigation(
        modifier = Modifier.navigationBarsPadding() // Add padding to avoid overlapping with the navigation bar
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                label = { Text(text = item) },
                selected = currentRoute.toString() == item, // Adjust according to your actual screen naming
                onClick = { onItemSelected(item) },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = screenIcons[item]?: Icons.Filled.Home,
                        contentDescription = item
                    )
                }
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewMyBottomNavigationBar() {
    val items = listOf("Posts", "Favorites", "Profile")
    // Use a sample Screen, like PostsScreen
    MyBottomNavigationBar(items = items, currentRoute = Screen.PostsScreen) {
        // Handle item selection (no-op for preview)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(title: String, onSearchClicked: () -> Unit = {}, onMoreClicked: () -> Unit = {}) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = onSearchClicked) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = onMoreClicked) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
            }
        }
    )
}

