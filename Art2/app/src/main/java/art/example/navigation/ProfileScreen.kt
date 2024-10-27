package art.example.navigation

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.api.data.Folder
import art.example.api.data.Post
import art.example.api.data.User
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.compose.viewModel


@Composable
fun MyProfile(navController: NavHostController) {
    val userViewModel: UserViewModel = koinViewModel()
    val currentUser by userViewModel.currentUser.observeAsState()
    val isLoading by userViewModel.isLoading.observeAsState(false)
    val isLoadingPost by userViewModel.isLoadingPosts.observeAsState(false)

    val userFolders by userViewModel.userFolders.observeAsState()

    // State to manage the selected tab index
    val selectedTabIndex = remember { mutableIntStateOf(0) }


    // for getting current user from database
    LaunchedEffect(Unit) {
        Log.d("MyProfile", "Loading current user")
        userViewModel.getCurrentUser()
    }

    LaunchedEffect(selectedTabIndex.intValue) {
        if (selectedTabIndex.intValue == 1) {
            Log.d("FLOW", "Loading user folders")
            userViewModel.getUserFolders()
        }
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "My Profile",
                showBackButton = false,
                onSearchClicked = { /* Handle search */ },
                onMoreClicked = { /* Handle more options */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        // Main container
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Upper box for user profile info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Space between profile and tabs
            ) {
                if (isLoading) {
                    // Show loading indicator for user profile
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    currentUser?.let { user ->
                        UserCard(
                            user = user
                        )
                    } ?: run {
                        Text(text = "User not found", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            // Lower box for toggling between posts and folders
            Column(modifier = Modifier.fillMaxSize()) {
                // TabRow for switching between posts and folders
                TabRow(selectedTabIndex = selectedTabIndex.intValue) {
                    Tab(
                        selected = selectedTabIndex.intValue == 0,
                        onClick = { selectedTabIndex.intValue = 0 },
                        text = { Text("Posts") }
                    )
                    Tab(
                        selected = selectedTabIndex.intValue == 1,
                        onClick = { selectedTabIndex.intValue = 1 },
                        text = { Text("Folders") }
                    )
                }

                // Display the selected content based on the selected tab
                when (selectedTabIndex.intValue) {
                    0 -> UserPostsList(usersPosts = currentUser?.posts, navController) // Pass loading status
                    1 -> userFolders?.let { UserFoldersList(userFolders = it, navController, isLoadingPost) } // Your user folders list implementation
                }
            }
        }
    }
}


@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.username, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = user.email, fontSize = 16.sp, color = Color.Gray)

            user.preferredTags?.let { tags ->
                if (tags.isNotEmpty()){
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preferred Tags:", fontSize = 16.sp, color = Color.Black)
                    tags.forEach { tag ->
                        TagBox(tag.name)
                    }
                }
            }
        }
    }
}


@Composable
fun UserPostsList(
    usersPosts: List<Post>?,
    navController: NavHostController,
    menuItems: List<MenuItem> = emptyList()
) {

    if (usersPosts.isNullOrEmpty()){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(text = "No Posts", fontSize = 20.sp, color = Color.Gray)
        }
    } else {
        // Implement your user posts list UI here
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(usersPosts) { post ->
                // Display each post in the grid
                    PostCard(post = post, onClick = {
                        // Navigate to post details when the post is clicked
                        navController.navigate(Screen.PostDetail.createRoute(post.id)) // Use the post ID to navigate
                    },
                        menuItems = menuItems
                    )
            }
        }
    }
}



@Composable
fun UserFoldersList(userFolders: List<Folder>, navController: NavHostController, isLoadingPost: Boolean) {

    if (isLoadingPost){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
    // Implement your user folders list UI here
        if (userFolders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(text = "No Folders", fontSize = 20.sp, color = Color.Gray)
            }
        } else {
                LazyVerticalGrid (
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(userFolders) { folder ->
                        FolderCard(folder = folder, onClick = {navController.navigate(Screen.FolderDetail.createRoute(folderId = folder.id))})
                    }
                }

            CreateFolderButton(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                onClick = {
                    navController.navigate(Screen.CreateFolder.route)
                }
            )
        }
    }
}


@Composable
fun FolderCard(
    folder: Folder,
    onClick : () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = folder.title, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = folder.description, fontSize = 16.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CreateFolderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
