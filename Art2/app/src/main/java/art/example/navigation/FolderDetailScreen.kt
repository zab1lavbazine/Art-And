package art.example.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import art.example.ViewModel.UserViewModel
import art.example.api.data.Post
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FolderDetailScreen(
    folderId: Long,
    navController: NavController
) {
    val userViewModel: UserViewModel = koinViewModel()
    val selectedFolder by userViewModel.selectedFolder.observeAsState()
    val isLoadingFolder by userViewModel.isLoadingFolder.observeAsState(initial = false)
    val errorMessage by userViewModel.errorMessage.observeAsState()

    // Fetch folder details when the folderId changes
    LaunchedEffect(folderId) {
        userViewModel.getDetailedFolder(folderId)
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Detailed folder",
                showBackButton = true,
                onSearchClicked = { navController.navigate(Screen.SearchScreen.route) },
                onMoreClicked = { /* Handle more options */ },
                onBackClicked = { navController.popBackStack()}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoadingFolder) {
                CircularProgressIndicator()
            } else {
                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                selectedFolder?.let { folder ->
                    // Card to display folder information
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.elevatedCardElevation(4.dp) // Elevation for shadow effect
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = folder.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = folder.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Display posts related to the folder
                    Text(text = "Posts:", style = MaterialTheme.typography.titleMedium)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Adjust for desired columns
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(folder.posts ?: emptyList()) { post ->
                            val menuItems = listOf(
                                MenuItem(
                                    label = "Delete post",
                                    onClick = {
                                        userViewModel.deletePostFromFolder(post, folder)
                                    }
                                )
                            )
                            PostCard(
                                post = post,
                                onClick = {
                                    navController.navigate(Screen.PostDetail.createRoute(post.id))
                                },
                                menuItems = menuItems
                            )
                        }
                    }
                } ?: run {
                    Text(text = "Folder not found.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
