package com.example.arthub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.arthub.screen.Screen


@Composable
fun Navigation(
    currentScreen: Screen,
    onNavigateToPostDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentScreen) {
        is Screen.PostsScreen -> {
            PostListScreen(
                onPostSelected = { postId ->
                    onNavigateToPostDetail(postId)
                },
                modifier = modifier // Use the provided modifier here
            )
        }
        is Screen.PostDetail -> {
            PostDetailScreen(id = currentScreen.postId) {
                onNavigateBack()
            }
        }
    }
}

