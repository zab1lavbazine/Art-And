package art.example.api.reponses

import androidx.paging.PagingSource
import androidx.paging.PagingState
import art.example.api.data.Post
import art.example.api.repository.impl.PostRepository

class SearchPagingSource(
    private val postRepository: PostRepository,
    private val pageSize: Int = 5,
    private val query: String
) : PagingSource<Int, Post>()
{
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val pageNumber = params.key ?: 0
            val response = postRepository.searchPosts(query, pageSize, pageNumber)

            val nextKey = if (response.content.isEmpty()) null else pageNumber + 1
            val prevkey = if (pageNumber == 0) null else pageNumber - 1

            LoadResult.Page(
                data = response.content,
                prevKey = prevkey,
                nextKey = nextKey
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }


}