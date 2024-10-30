package art.example.api.service

import art.example.api.data.DTO.FolderDTO
import art.example.api.data.Folder
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FolderApiService {


    @GET("/api/folders")
    suspend fun getFolders(): List<Folder>

    @GET("/api/folders/{id}")
    suspend fun getFolderById(@Path("id") id: Long): Folder?

    @GET("/api/folders/user")
    suspend fun getFoldersByUser(): List<Folder>

    @POST("/api/folders")
    suspend fun createFolder( @Body folderDTO: FolderDTO): Folder?

    @PUT("/api/folders/{id}")
    suspend fun updateFolderById(@Path("id") folderId: Long, @Body folderDTO: FolderDTO) : Folder?


    @DELETE("/api/folders/{id}")
    suspend fun deleteFolderById(@Path("id") id : Long)
}