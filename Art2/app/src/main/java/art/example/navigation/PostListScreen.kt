package art.example.navigation

import android.annotation.SuppressLint
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.api.data.Post
import art.example.screen.Screen
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.koinViewModel
import coil.compose.rememberImagePainter // Import Coil for image loading




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    val viewModel: PostViewModel = koinViewModel()
    val posts by viewModel.posts.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)

    val pullRefreshState = rememberPullRefreshState(refreshing = isLoading, onRefresh = {
        viewModel.updatePosts() // Refresh posts
    })


    LaunchedEffect(Unit) {
        viewModel.loadPost()
    }


    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Posts",
                showBackButton = false,
                onSearchClicked = { navController.navigate(Screen.SearchScreen.route) },
                onMoreClicked = { /* Handle more options */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        // Add padding to account for top and bottom bars
        Box (
            modifier = Modifier
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            // Display a grid layout of posts
            if (!isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // Adjust to your desired column count
                    modifier = modifier.fillMaxSize(), // Use the provided modifier
                    contentPadding = PaddingValues(8.dp) // Padding around the grid
                ) {
                    items(posts) { post ->
                        PostCard(post = post, onClick = {
                            // Navigate to the PostDetail screen when the post is clicked
                            navController.navigate(Screen.PostDetail.createRoute(post.id))
                        })
                    }
                }
            } else {
                // Show a loading spinner while posts are loading
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Composable
fun PostCard(post: Post, onClick: () -> Unit) {
    // Get the screen width in pixels
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Calculate the maximum card width as a fraction of the screen width
    val maxCardWidth = screenWidth * 0.45f // Adjust the fraction as needed (e.g., 0.45 for 2 cards in a row)
    val maxCardHeight = 200.dp // Set your desired maximum height

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .widthIn(max = maxCardWidth) // Set dynamic max width based on screen size
            .heightIn(max = maxCardHeight) // Limit the maximum height
    ) {
        // Check if the post has an image URL
        if (post.imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl), // Use imageUrl instead of image
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxSize() // Fill the entire card
                    .background(Color.Gray), // Background color while loading
                contentScale = ContentScale.Crop // Crop the image to fit
            )
        } else {
            // Optionally, display a placeholder or a default image if imageUrl is null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray), // Placeholder color
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Image", color = Color.White) // Placeholder text
            }
        }
    }
}

