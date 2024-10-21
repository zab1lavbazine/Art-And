package art.example.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import art.example.api.data.Post
import art.example.api.repository.impl.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository,
    private val context : Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

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
                Log.d("FLOW", "in the view model getting posts")
                val fetchedPosts = postRepository.getPosts()
                _posts.value = fetchedPosts
            } catch (e : Exception){
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun loadById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val post = postRepository.getPostById(id)
                _selectedPost.value = post
            } catch (e: Exception) {
                // Handle
                _selectedPost.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}