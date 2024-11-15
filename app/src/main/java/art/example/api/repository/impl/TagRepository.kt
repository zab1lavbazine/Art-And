package art.example.api.repository.impl

import android.util.Log
import art.example.api.data.Tag
import art.example.api.data.toTagEntity
import art.example.api.service.TagApiService
import art.example.database.TagDao.TagDao
import art.example.database.entities.toTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagRepository(
    private val tagDao: TagDao,
    private val tagApiService: TagApiService
) {

    suspend fun loadTags(): List<Tag>{
        try {
            val tags = tagDao.loadTags()
            if (tags.isNotEmpty()){
                return tags.map { it.toTag() }
            }

            val tagsApi = tagApiService.getTags()
            if (tagsApi.isNotEmpty()){
                saveTags(tagsApi)
                return tagsApi
            }
            return emptyList()
        } catch (e: Exception){
            return emptyList()
        }
    }



    suspend fun saveTags(tags: List<Tag>) {
        withContext(Dispatchers.IO){
            try {
                tagDao.insertTags(tags.map { it.toTagEntity() })
            }catch (e: Exception){
                Log.d("FLOW", "Error with saving tags")
            }
        }
    }

}