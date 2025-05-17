package art.example.navigation.folderScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import art.example.ViewModel.FolderViewModel
import art.example.ViewModel.UserViewModel
import art.example.modules.MenuItemBuilder
import art.example.navigation.GeneralMenuItem
import art.example.navigation.MenuItem
import art.example.navigation.MyTopAppBar
import art.example.navigation.postScreen.PostCard
import art.example.navigation.supportElements.MyModalBottomSheet
import art.example.screen.MiscScreens
import art.example.screen.PostScreens
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun FolderDetailScreen(
    folderId: Long,
    navController: NavController
) {
    val folderViewModel: FolderViewModel = koinViewModel()
    val selectedFolder by folderViewModel.selectedFolder.observeAsState()
    val isLoadingFolder by folderViewModel.isLoading.observeAsState(initial = false)
    val errorMessage by folderViewModel.errorMessage.observeAsState()


    var isEditMode by remember { mutableStateOf(false) }
    var folderTitle by remember { mutableStateOf("") }
    var folderDescription by remember { mutableStateOf("") }
    var warningMessage by remember { mutableStateOf("") }

    // State to control bottom sheet visibility
    var showBottomSheet by remember { mutableStateOf(false) }

    // Fetch folder details when the folderId changes
    LaunchedEffect(folderId) {
        folderViewModel.getDetailedFolderById(folderId)
    }


    val menuItems = listOf(
        GeneralMenuItem(
            label = "Update folder info",
            onClickAction = {
                selectedFolder?.let{ folder ->
                    folderTitle = folder.title
                    folderDescription = folder.description
                }
                showBottomSheet = false
                isEditMode = true
            }
        ),
        GeneralMenuItem(
            label = "Delete folder",
            onClickAction = {
                showBottomSheet = false
                selectedFolder?.let { folder ->
                    folderViewModel.deleteFolderById(folder.id)
                    navController.popBackStack()
                }
            }
        )
    )

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Detailed folder",
                showBackButton = true,
                onSearchClicked = { navController.navigate(MiscScreens.SearchScreen.route) },
                showMoreClickedButton = true,
                onMoreClicked = {
                    showBottomSheet = true
                }, // Show bottom sheet on more button click
                onBackClicked = { navController.popBackStack() }
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
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = folder.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Display posts related to the folder
                    Text(
                        text = "Posts:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.LightGray
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Adjust for desired columns
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(folder.posts) { post ->
                            // adding new menu item to delete post from folder
                            val postMenuItems = MenuItemBuilder().addItem(label = "Delete post", onClick = {folderViewModel.deletePostFromFolder(post, folder)}).build()
                            PostCard(
                                post = post,
                                onClick = {
                                    navController.navigate(PostScreens.PostDetail.createRoute(post.id))
                                },
                                menuItems = postMenuItems
                            )
                        }
                    }
                } ?: run {
                    Text(text = "Folder not found.", style = MaterialTheme.typography.bodyLarge)
                }

                // Bottom sheet for additional options

                MyModalBottomSheet(
                    showDialog = showBottomSheet,
                    onDismissRequest = { showBottomSheet = false },
                    menuItems = menuItems
                )


                if (isEditMode) {
                    Dialog(onDismissRequest = { isEditMode = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Update Folder",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = folderTitle,
                                    onValueChange = {
                                        folderTitle = it
                                                    warningMessage = ""
                                                    },
                                    label = { Text("Folder Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = folderDescription,
                                    onValueChange = { folderDescription = it },
                                    label = { Text("Description") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                // show warning
                                if (warningMessage.isNotEmpty()){
                                    Text(
                                        text = warningMessage,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = { isEditMode = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Cancel")
                                    }

                                    Button(
                                        onClick = {
                                            if (folderTitle.isBlank()){
                                                warningMessage = "Folder title is required."
                                            } else {
                                                folderViewModel.updateFolderInfo(
                                                    id = folderId,
                                                    title = folderTitle,
                                                    description = folderDescription
                                                )
                                                isEditMode = false
                                            }
                                        }
                                    ) {
                                        Text("Submit")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                errorMessage?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

