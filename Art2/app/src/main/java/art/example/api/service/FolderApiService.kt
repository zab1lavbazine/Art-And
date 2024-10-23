package art.example.api.service

import art.example.api.data.DTO.FolderDTO
import art.example.api.data.Folder
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FolderApiService {


    @GET("/api/folders")
    suspend fun getFolders(): List<Folder>

    @GET("/api/folders/{id}")
    suspend fun getFolderById(id: Long): Folder?

    @GET("/api/folders/user")
    suspend fun getFoldersByUser(): List<Folder>

    @POST("/api/folders")
    suspend fun createFolder( @Body folderDTO: FolderDTO): Folder?
}