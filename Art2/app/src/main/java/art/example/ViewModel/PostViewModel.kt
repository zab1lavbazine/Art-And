package art.example.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import art.example.api.data.Tag
import art.example.api.repository.impl.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository,
    private val context : Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // for the selected post, it will update and then be present for the UI
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: MutableLiveData<Post?> get() = _selectedPost

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>> get() = _posts

    private val _tags = MutableLiveData<List<Tag>>()
    val tags : LiveData<List<Tag>> get() = _tags

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading

    fun loadPost(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("FLOW", "in the view model GETTING posts")
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
            _isLoading.value = true
            try {
                val post = postRepository.getPostById(id)
                _selectedPost.value = post
            } catch (e: Exception) {
                Log.d("FLOW", "EXCEPTION while getting post by id : $id")
                _selectedPost.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun submitPost(postDTO: PostDTO, imageUrl : String){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                postRepository.createPost(postDTO, imageUrl)
                _errorMessage.value = "Post submitted successfully!"
            } catch (e: Exception){
                _errorMessage.value = "Submission failed: ${e.message}"
                Log.d("FLOW", "POST post failed for postDTO:${postDTO}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTags(){
        viewModelScope.launch {
            try {
                val fetchedTags = postRepository.loadTags() // Fetch tags from your repository
                _tags.value = fetchedTags
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    fun updatePosts(){
        viewModelScope.launch{
            _isLoading.value = true
            try {
                Log.d("FLOW", "in the view model UPDATING posts")
                val fetchedPosts = postRepository.updateDatabasePosts()
                _posts.value = fetchedPosts
            } catch (e : Exception){
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}