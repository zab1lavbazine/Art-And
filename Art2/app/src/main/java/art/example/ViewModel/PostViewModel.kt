package art.example.ViewModel

import android.content.Context
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
import art.example.api.data.Tag
import art.example.api.repository.impl.PostRepository
import art.example.api.responses.PostPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _tags = MutableLiveData<List<Tag>>()
    val tags : LiveData<List<Tag>> get() = _tags

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> = _isLoading



    private fun getPostsStream(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 20), // Set the desired page size
            pagingSourceFactory = { PostPagingSource(postRepository, pageSize = 20) }
        ).flow.cachedIn(viewModelScope) // caching in the view model scope that data from the server
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


//    fun updatePosts(){
//        viewModelScope.launch{
//            _isLoading.value = true
//            try {
//                Log.d("FLOW", "in the view model UPDATING posts")
//                val fetchedPosts = postRepository.updateDatabasePosts()
//                _posts.value = fetchedPosts
//            } catch (e : Exception){
//                // handle error
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
}