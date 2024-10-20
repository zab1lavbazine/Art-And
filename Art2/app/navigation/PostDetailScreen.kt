package com.example.arthub.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arthub.ViewModel.PostViewModel
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PostDetailScreen(id: Long, onBack: () -> Unit){
    val viewModel: PostViewModel = koinViewModel()

    val selectedPost by viewModel.selectedPost.observeAsState()


    LaunchedEffect(id) {
        viewModel.loadById(id)
    }

    Scaffold {
        selectedPost?.let { post ->
            BasicText(text = "Title: ${post.title}") // Display post title
            BasicText(text = "Content: ${post.description}") // Display post content
            // Add other fields as needed
        } ?: run {
            BasicText(text = "Loading...") // Loading state
        }
    }

    Button(onClick = { onBack()}){
        BasicText(text = "Back")
    }
}