package com.example.arthub.screen

sealed interface Screen {
    object PostsScreen: Screen
    data class PostDetail(val postId:Long) :Screen
}


