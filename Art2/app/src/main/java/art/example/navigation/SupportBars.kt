package art.example.navigation


import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import art.example.screen.Screen




val screenIcons = mapOf(
    "Posts" to Icons.Filled.Home,
    "Favorites" to Icons.Filled.Favorite,
    "Profile" to Icons.Filled.Person
)

@Composable
fun MyBottomNavigationBar(
    items: List<String>,
    currentRoute: String?, // Update to String
    onItemSelected: (String) -> Unit
) {
    BottomNavigation(
        modifier = Modifier.navigationBarsPadding() // Add padding to avoid overlapping with the navigation bar
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                label = { Text(text = item) },
                selected = currentRoute == item, // Compare with item string
                onClick = { onItemSelected(item) },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = screenIcons[item] ?: Icons.Filled.Home, // Default icon
                        contentDescription = item
                    )
                }
            )
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    MyBottomNavigationBar(
        items = listOf("Posts", "New", "Profile"),
        currentRoute = navController.currentDestination?.route, // Get the current route as a string
        onItemSelected = { selectedItem ->
            when (selectedItem) {
                "Posts" -> navController.navigate(Screen.PostsScreen.route) {
                    // Clear the back stack if needed
                    popUpTo(Screen.PostsScreen.route) { inclusive = true }
                }
                "New" -> navController.navigate(Screen.CreatePost.route){
                    popUpTo(Screen.PostsScreen.route){ inclusive = true}
                }
                "Profile" -> navController.navigate(Screen.MyProfile.route) {
                    // Clear the back stack if needed
                    popUpTo(Screen.MyProfile.route) { inclusive = true }
                }
                // Add other screens as needed
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    title: String,
    showBackButton: Boolean = false,
    onSearchClicked: () -> Unit = {},
    onMoreClicked: () -> Unit = {},
    onBackClicked: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton && onBackClicked != null) {
                IconButton(onClick = onBackClicked) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
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
