package art.example.navigation

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import art.example.ViewModel.UserViewModel
import art.example.api.data.Post
import art.example.api.data.User
import art.example.screen.Screen
import org.koin.androidx.compose.koinViewModel




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
fun MyProfile(navController: NavHostController) {
    val userViewModel: UserViewModel = koinViewModel()
    val currentUser by userViewModel.currentUser.observeAsState()
    val isLoading by userViewModel.isLoading.observeAsState(true)

    // State to manage the selected tab index
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        Log.d("MyProfile", "Loading current user")
        userViewModel.getCurrentUser()
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
                    1 -> UserFoldersList() // Your user folders list implementation
                }
            }
        }
    }
}

@Composable
fun UserPostsList(usersPosts: List<Post>?, navController: NavHostController) {

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
                    })
            }
        }
    }
}



@Composable
fun UserFoldersList() {
    // Implement your user folders list UI here
    LazyColumn {
        items(10) { index ->
            Text(text = "Folder #$index", modifier = Modifier.padding(8.dp))
        }
    }
}