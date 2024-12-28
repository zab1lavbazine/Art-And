package art.example.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import art.example.api.data.DTO.PostDTO
import art.example.api.data.Post
import art.example.api.reponses.SearchPagingSource
import art.example.api.repository.impl.PostRepository
import art.example.api.responses.PostPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository,
    private val context : Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // for the selected post, it will update and then be present for the UI
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: MutableLiveData<Post?> get() = _selectedPost

//    private val _posts : Flow<PagingData<Post>> = getPostsStream()
    val posts : Flow<PagingData<Post>> = getPostsStream()


    private val _selectedPosts = MutableLiveData<List<Post>>()
    val selectedPosts: MutableLiveData<List<Post>> get() = _selectedPosts

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading



    fun getCurrentUserPosts(){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUserId = sharedPreferences.getLong("current_user_id", -1)
                val posts = postRepository.getPostsByUserId(currentUserId)
                _selectedPosts.value = posts
            } catch (e: Exception){
                Log.d("FLOW", "Error fetching posts for current user")
                _selectedPosts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserPostsById(userId: Long){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val currentUserPosts = postRepository.getPostsByUserId(userId)
                _selectedPosts.value = currentUserPosts
            } catch (e : Exception){
                Log.d("FLOW", "Failed to get")
                _selectedPosts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchPosts(query: String): Flow<PagingData<Post>> {
        return getSearchStream(query)
    }

    private fun getPostsStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 5), // Set the desired page size
            pagingSourceFactory = { PostPagingSource(postRepository, pageSize = 5) }
        ).flow.cachedIn(viewModelScope) // caching in the view model scope that data from the server
    }

    private fun getSearchStream(query: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { SearchPagingSource(postRepository = postRepository, query = query)}
        ).flow.cachedIn(viewModelScope)
    }

    fun deletePostById(postId: Long){
        viewModelScope.launch {
            try {
                postRepository.deletePostById(postId)
            } catch (e: Exception){
                Log.d("FLOW", "Error deleting post: $postId")
            }
        }
    }


    fun updatePost(updatedPost: Post){
        viewModelScope.launch {
            try {
                Log.d("FLOW", "Updating post: $updatedPost")
                val newPost = postRepository.updatePost(context = context, post = updatedPost)
                _selectedPost.value = newPost
                Log.d("FLOW", "Updating post: $newPost")
            } catch (e: Exception){
                Log.d("FLOW", "update post failed: ${e.message}")
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

    fun submitPost(postDTO: PostDTO, imageBitmap: Bitmap){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                postRepository.createPost(postDTO, imageBitmap)
                _errorMessage.value = "Post submitted successfully!"
            } catch (e: Exception){
                _errorMessage.value = "Submission failed: ${e.message}"
                Log.d("FLOW", "POST post failed for postDTO:${postDTO}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}