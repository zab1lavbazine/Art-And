package art.example.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import art.example.api.data.Comment
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

    private val _selectedPostComments = MutableLiveData<MutableList<Comment>>()
    val selectedPostComments: MutableLiveData<MutableList<Comment>> get() = _selectedPostComments

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
                Log.d("FLOW", "FETCHED posts for user: $currentUserId, posts: $posts")
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
                Log.d("FLOW", "Error to get posts for userId: $userId, cause: ${e.message}")
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

    fun getPostCommentsByPostId(postId: Long ){
        viewModelScope.launch {
            try {
                val comments = postRepository.getPostCommentsByPostId(postId)
                Log.d("FLOW", "Comments for post: $postId, $comments")
                _selectedPostComments.value = comments.toMutableList()
            } catch (e : Exception){
                Log.d("FLOW", "Error getting post comments, postId: $postId")
            }
        }
    }

    fun postNewCommentUnderPostWithId( postId: Long, comment: String){
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val newComment = postRepository.postNewCommentUnderPostWithId( postId, comment)
                newComment?.let {
                    val updatedComments =
                        _selectedPostComments.value?.toMutableList() ?: mutableListOf()
                    updatedComments.add(newComment)
                    _selectedPostComments.value = updatedComments
                }
            } catch (e: Exception){
                Log.d("FLOW", "Failed to post new comment: $comment for post with id: $postId")
                Log.d("FLOW", "ERROR: ${e.message}")
                _errorMessage.value = "Failed to post comment"
            }
        }
    }

    fun deleteCommentByIdFromPost(postId : Long, commentId: Long){
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                postRepository.deleteCommentByIdFromPost(postId, commentId)
                val updatedComments = _selectedPostComments.value?.filter { comment -> comment.id != commentId} ?: emptyList()
                _selectedPostComments.value = updatedComments.toMutableList()
                Log.d("FLOW", "Comment $commentId deleted from post: $postId")
            } catch (e: Exception){
                Log.d("FLOW", "Failed to delete comment: $commentId, from post: $postId")
                _errorMessage.value = "Failed to delete comment from post"
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