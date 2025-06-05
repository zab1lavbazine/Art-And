package art.example.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.screen.PostScreens
import art.example.screen.Screen
import art.example.screen.UserScreens
import cz.fit.cvut.feature.language.presentation.TolgeeLanguageDropdown
import cz.fit.cvut.feature.translation.presentation.common.component.Translate
import cz.fit.cvut.feature.translation.presentation.common.component.t
import kotlinx.coroutines.launch


enum class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    POSTS("Posts", Icons.Filled.Home, PostScreens.PostsScreen.route),
    NEW("New", Icons.Filled.Add, PostScreens.CreatePost.route),
    PROFILE("Profile", Icons.Filled.Person, UserScreens.MyProfile.route)
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
                    Translate(
                        keyName = item.title,
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
    title: AnnotatedString,
    showBackButton: Boolean = false,
    showSearchButton: Boolean = false,
    showMoreClickedButton: Boolean = false,
    onSearchClicked: (() -> Unit)? = null,
    onMoreClicked: (() -> Unit)? = null,
    onBackClicked: (() -> Unit)? = null,
    menuItems: List<MenuItem> = emptyList()
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false },
            sheetState = sheetState
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                TolgeeLanguageDropdown()
            }
        }
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.primary
            )
        },
        navigationIcon = {
            if (showBackButton && onBackClicked != null) {
                IconButton(onClick = onBackClicked) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (showSearchButton && onSearchClicked != null) {
                IconButton(onClick = onSearchClicked) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
            }

            IconButton(onClick = {
                scope.launch {
                    showLanguageSheet = true
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Choose Language"
                )
            }

            if (showMoreClickedButton && onMoreClicked != null) {
                IconButton(onClick = onMoreClicked) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More options")
                }
            }
        }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        menuItems.forEach { menuItem ->
            DropdownMenuItem(
                text = { Text(menuItem.label) },
                onClick = {
                    menuItem.onClick()
                    expanded = false
                }
            )
        }
    }
}




abstract class MenuItem(val label: String){
    abstract fun onClick()
}

class GeneralMenuItem(
    label: String,
    private val onClickAction: () -> Unit
): MenuItem(label){
    override fun onClick() {
        onClickAction()
    }
}

class ExtendedMenuItem<T>(
    label: String,
    private val onClickAction: (T) -> Unit,
    var elementId: T
): MenuItem(label) {
    override fun onClick() {
        onClickAction(elementId)
    }
}

//data class MenuItem(
//    val label: String,
//    val onClick: () -> Unit
//)
