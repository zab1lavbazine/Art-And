package com.example.arthub.navigation

import android.annotation.SuppressLint
import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import coil.compose.rememberAsyncImagePainter
import com.example.arthub.ViewModel.PostViewModel
import org.koin.androidx.compose.koinViewModel
import coil.compose.rememberImagePainter // Import Coil for image loading
import com.example.arthub.api.data.Post


@Composable
fun PostListScreen(onPostSelected: (Long) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: PostViewModel = koinViewModel()
    val posts by viewModel.posts.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(true)

    LaunchedEffect(Unit) {
        viewModel.loadPost()
    }

    // Display a grid layout of posts
    if (!isLoading) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // Two columns in the grid
            modifier = modifier.fillMaxSize(), // Use the provided modifier
            contentPadding = PaddingValues(8.dp) // Padding around the grid
        ) {
            items(posts) { post ->
                PostCard(post = post, onClick = { onPostSelected(post.id) })
            }
        }
    } else {
        // Show a loading spinner while posts are loading
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary

        )
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
        Image(
            painter = rememberAsyncImagePainter(post.image), // Replace with actual field
            contentDescription = post.title,
            modifier = Modifier
                .fillMaxSize() // Fill the entire card
                .background(Color.Gray), // Background color while loading
            contentScale = ContentScale.Crop // Crop the image to fit
        )
    }
}
