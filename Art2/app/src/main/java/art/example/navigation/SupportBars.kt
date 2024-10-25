package art.example.navigation


import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import art.example.screen.Screen










enum class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    POSTS("Posts", Icons.Filled.Home, Screen.PostsScreen.route),
    NEW("New", Icons.Filled.Add, Screen.CreatePost.route),
    PROFILE("Profile", Icons.Filled.Person, Screen.MyProfile.route)
}



@Composable
fun MyBottomNavigationBar(
    items: List<NavigationItem>,
    currentRoute: String?, // Update to String
    onItemSelected: (NavigationItem) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.navigationBarsPadding() // Add padding to avoid overlapping with the navigation bar
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                label = {
                    Text(
                        text = item.title,
                        color = if (currentRoute == item.route) MaterialTheme.colors.onPrimary else Color.LightGray
                    )
                        },
                selected = currentRoute == item.route, // Compare with item string
                onClick = { onItemSelected(item) },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    MyBottomNavigationBar(
        items = NavigationItem.entries, // Use enum values
        currentRoute = navController.currentDestination?.route,
        onItemSelected = { selectedItem ->
            if (navController.currentDestination?.route != selectedItem.route) {
                navController.navigate(selectedItem.route) {
                    // Clear the back stack if needed
                    popUpTo(selectedItem.route) { inclusive = true }
                }
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
        title = { Text(
            text = title,
            color = MaterialTheme.colors.primary
            ) },
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
