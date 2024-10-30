package art.example.navigation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.data.Folder
import art.example.api.data.Post
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.items


import org.koin.androidx.compose.koinViewModel


@Composable
fun PostDetailScreen(
    postId: Long,
    navController: NavHostController
) {
    // Get the view model for this screen
    val viewModel: PostViewModel = koinViewModel()
    val userViewModel: UserViewModel = koinViewModel()

    // Observe the selected post and loading state
    val selectedPost by viewModel.selectedPost.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = true) // Use an initial value


    // for the folder dialog
    val userFolders by userViewModel.userFolders.observeAsState(emptyList())
    val showDialog = remember { mutableStateOf(false) }

    // Load the post details when the postId changes
    LaunchedEffect(postId) {
        viewModel.loadById(postId)
    }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value) {
            Log.d("FLOW", "Loading user folders")
            userViewModel.getUserFolders()
        }
    }

    // Create the Scaffold for the screen layout
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Post details",
                showBackButton = true,
                onSearchClicked = { /* Handle search */ },
                onMoreClicked = { /*  more options */ },
                onBackClicked = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                selectedPost?.let { post ->
                            PostCardItem(
                                post = post,
                                onAddToFolderClick = { selectedFolder ->
                                    userViewModel.savePostInFolder(post, selectedFolder)
                                    showDialog.value = false // Hide dialog after saving
                                },
                                showDialog = showDialog,
                                userFolders = userFolders
                            )
                        }
                    }
                }

            }
        }



@Composable
fun PostCardItem(
    post: Post,
    onAddToFolderClick: (Folder) -> Unit,
    showDialog: MutableState<Boolean>,
    userFolders: List<Folder>
) {
    // Wrap the Column with verticalScroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Load image from URL or ByteArray
        if (post.imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                contentScale = ContentScale.Fit
            )
        } else if (post.image?.data != null) {
            val imageBitmap = byteArrayToImageBitmap(post.image.data)
            Image(
                bitmap = imageBitmap,
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                contentScale = ContentScale.Fit
            )
        } else {
            // Placeholder in case there's no image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .background(Color.Gray) // Placeholder color
            ) {
                Text(
                    text = "No Image Available",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }

        // Post details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = post.title,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.description,
                fontSize = 16.sp,
                color = Color.LightGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                post.tags?.forEach { tag ->
                    TagBox(tag.name)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Add button at the bottom of the Box
            Button(
                onClick = { showDialog.value = true },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(bottom = 16.dp) // Add padding from bottom
            ) {
                Text(text = "Add to Folder")
            }

            // Folder Selection Dialog
            if (showDialog.value) {
                FolderSelectionDialog(
                    userFolders = userFolders,
                    onDismiss = { showDialog.value = false },
                    onFolderSelected = { selectedFolder ->
                        onAddToFolderClick(selectedFolder)
                    }
                )
            }
        }
    }
}





@Composable
fun FolderSelectionDialog(
    userFolders: List<Folder>,
    onDismiss: () -> Unit,
    onFolderSelected: (Folder) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Folder") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(min = 100.dp) // Optional: set a minimum height
            ) {
                // Correct usage of items for a list of folders
                items(userFolders) { folder ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                onFolderSelected(folder)
                            }
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = folder.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



@Composable
fun TagBox(tag: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.small) // Light gray background with rounded corners
            .padding(8.dp) // Padding inside the box
    ) {
        Text(
            text = tag,
            color = Color.Black,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap {
    val bitmap: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    return bitmap.asImageBitmap()
}