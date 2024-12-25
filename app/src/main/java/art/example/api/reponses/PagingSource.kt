package art.example.api.responses

import androidx.paging.PagingSource
import androidx.paging.PagingState
import art.example.api.data.Post
import art.example.api.repository.impl.PostRepository

class PostPagingSource(
    private val postRepository: PostRepository,
    private val pageSize: Int = 5 // todo() change to 20 , 5 is testing example
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            // Determine the page number to load
            val pageNumber = params.key ?: 0 // Default to page 0 if key is null

            val response = postRepository.getPosts(pageSize, pageNumber)

            val nextKey = if (response.content.isEmpty()) null else pageNumber + 1
            val prevKey = if (pageNumber == 0) null else pageNumber - 1


            LoadResult.Page(
                data = response.content,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        // This key will be used when refreshing the data
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }
}
