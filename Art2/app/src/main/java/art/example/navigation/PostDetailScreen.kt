package art.example.navigation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import art.example.ViewModel.PostViewModel
import art.example.api.data.Post
import coil.compose.rememberAsyncImagePainter

import org.koin.androidx.compose.koinViewModel


@Composable
fun PostDetailScreen(postId: Long, navController: NavHostController) {
    // Get the view model for this screen
    val viewModel: PostViewModel = koinViewModel()

    // Observe the selected post and loading state
    val selectedPost by viewModel.selectedPost.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = true) // Use an initial value

    // Load the post details when the postId changes
    LaunchedEffect(postId) {
        Log.d("PostDetailScreen", "Loading post with ID: $postId")
        viewModel.loadById(postId)
    }

    // Create the Scaffold for the screen layout
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Post details",
                showBackButton = true,
                onSearchClicked = { /* Handle search */ },
                onMoreClicked = { /* Handle more options */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    // Show loading state
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) // Use a progress indicator
                }
                selectedPost != null -> {
                    // Show the post details
                    PostCardItem(post = selectedPost!!)
                }
                else -> {
                    // Post not found or could not load
                    Text(text = "Post not found", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}



@Composable
fun PostCardItem(post: Post) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
            } else if (post.image != null) {
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
                    .weight(0.2f)
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
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


fun byteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap {
    val bitmap: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    return bitmap.asImageBitmap()
}