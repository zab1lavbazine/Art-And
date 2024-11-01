package com.example.arthub.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arthub.api.RetrofitInstance
import com.example.arthub.api.data.Post
import com.example.arthub.api.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val postRepository: PostRepository) : ViewModel() {

    // for the selected post, it will update and then be present for the UI
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> get() = _selectedPost

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>> get() = _posts


    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading

    fun loadPost(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedPosts = postRepository.getPosts()
                _posts.value = fetchedPosts
            } catch (e : Exception){
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            try {
                val post = postRepository.getPostById(id)
                // Check if the fetched post is different from the current selected post
                if (_selectedPost.value != post) {
                    _selectedPost.value = post // Update LiveData with the new post
                }
            } catch (e: Exception) {
                // Handle
            }
        }
    }
}