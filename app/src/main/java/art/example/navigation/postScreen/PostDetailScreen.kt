package art.example.navigation.postScreen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.ViewModel.UserViewModel
import art.example.api.data.Folder
import art.example.api.data.Post
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.runtime.setValue
import art.example.navigation.BottomNavigationBar
import art.example.navigation.MenuItem
import art.example.navigation.MyTopAppBar
import art.example.navigation.supportElements.MyModalBottomSheet
import art.example.ViewModel.FolderViewModel
import art.example.ViewModel.TagViewModel
import art.example.api.data.Tag
import art.example.api.data.User
import art.example.navigation.supportElements.ResolvePostImage

import org.koin.androidx.compose.koinViewModel


@Composable
fun PostDetailScreen(
    postId: Long,
    navController: NavHostController
) {
    // Get the view model for this screen
    val postViewModel: PostViewModel = koinViewModel()
    val folderViewModel: FolderViewModel = koinViewModel()
    val userViewModel: UserViewModel = koinViewModel()
    val tagViewModel: TagViewModel = koinViewModel()

    val currentUser by userViewModel.currentUser.observeAsState()

    // Observe the selected post and loading state
    val selectedPost by postViewModel.selectedPost.observeAsState()
    val isLoading by postViewModel.isLoading.observeAsState(initial = true) // Use an initial value
    val tags by tagViewModel.tags.observeAsState(emptyList())

    // for the folder dialog
    val userFolders by folderViewModel.folders.observeAsState(emptyList())


    val showDialog = remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }


    val menuItems = mutableListOf(
        MenuItem(
            label = "Add to folder",
            onClick = {
                showDialog.value = true
            }
        )
    )

    selectedPost?.let { post ->
        if (post.patron.id == currentUser?.id) {
            menuItems.add(
                MenuItem(
                    label = "Edit post",
                    onClick = {
                        showEditDialog = true
                    }
                )
            )
            menuItems.add(
                MenuItem(
                    label = "Delete post",
                    onClick = {
                        postViewModel.deletePostById(postId)
                        navController.popBackStack()
                    }
                )
            )
        }
    }


    // Load the post details when the postId changes
    LaunchedEffect(postId) {
        postViewModel.loadById(postId)
        userViewModel.getCurrentUser()
        folderViewModel.getCurrentUserFolders()
        tagViewModel.loadTags()
    }


    // Create the Scaffold for the screen layout
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Post details",
                showBackButton = true,
                onSearchClicked = { /* Handle search */ },
                onMoreClicked = {
                    showBottomSheet = true
                },
                onBackClicked = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            selectedPost?.let { post ->
                PostCardItem(
                    post = post,
                    onAddToFolderClick = { selectedFolder ->
                        folderViewModel.savePostInFolder(post, selectedFolder)
                        showDialog.value = false // Hide dialog after saving
                    },
                    showDialog = showDialog,
                    userFolders = userFolders,
                    navController = navController
                )

            }
        }
    }
    MyModalBottomSheet(
        showDialog = showBottomSheet,
        onDismissRequest = {
            showBottomSheet = false
        },
        menuItems = menuItems
    )

    if (showEditDialog){
        EditPostDialog(
            post = selectedPost,
            tagsList = tags,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedPost ->
                postViewModel.updatePost(updatedPost)
                showEditDialog = false
            }
        )
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun EditPostDialog(
    post: Post?,
    tagsList: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (Post) -> Unit
) {
    var title by remember { mutableStateOf(post?.title ?: "") }
    var description by remember { mutableStateOf(post?.description ?: "") }

    val selectedTagIds = remember { mutableStateOf(post?.tags?.map { it.id }?.toSet() ?: emptySet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Post") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                TagSelectionMenu(
                    tags = tagsList,
                    selectedTags = selectedTagIds.value,
                    onTagSelected = { selectedTagIds.value = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    post?.copy(
                        title = title,
                        description = description,
                        tags = selectedTagIds.value.map { id ->
                            tagsList.find { it.id == id } ?: Tag(id, "Unknown")
                        }.toMutableList()
                    )?.let {
                        onConfirm(
                            it
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




@Composable
fun PostCardItem(
    post: Post,
    onAddToFolderClick: (Folder) -> Unit,
    showDialog: MutableState<Boolean>,
    userFolders: List<Folder>,
    navController: NavHostController
) {
    // Wrap the Column with verticalScroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        ResolvePostImage(post)

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Posted by:",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 4.dp)
                )

                UsernameTag(patron = post.patron, onClick = { Unit })
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                post.tags.forEach { tag ->
                    TagBox(tag.name)
                }
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
fun UsernameTag(patron: User, onClick: () -> Unit){
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = Color.LightGray,
                shape = MaterialTheme.shapes.small
            )
            .clickable{ onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ){
        Text(
            text = patron.username,
            color = Color.Black,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}


@Composable
fun TagBox(tag: String) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                Color.LightGray,
                shape = MaterialTheme.shapes.small
            ) // Light gray background with rounded corners
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


