package art.example.api.responses

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import art.example.api.data.Post
import art.example.api.repository.impl.PostRepository

class PostPagingSource(
    private val postRepository: PostRepository,
    private val pageSize: Int = 5 // todo() change to 20 , 5 is testing example
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        // Determine the page number to load
        val pageNumber = params.key ?: 0 // Default to page 0 if key is null

        return try {
            // Fetch posts from the repository
            val response = postRepository.getPosts(pageSize, pageNumber)

            Log.d("FLOW", "Response in the load: $response")

            LoadResult.Page(
                data = response,
                prevKey = if (pageNumber == 0) null else pageNumber - 1, // Previous page key
                nextKey = if (response.isEmpty()) null else pageNumber + 1 // Next page key
            )
        } catch (exception: Exception) {
            // Handle exceptions (e.g., log the error)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        // This key will be used when refreshing the data
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}
