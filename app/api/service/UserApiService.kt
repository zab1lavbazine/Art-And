package com.example.arthub.api.service

import com.example.arthub.api.data.User
import retrofit2.http.GET

interface UserApiService {

    @GET("/api/users")
    suspend fun getUsers():List<User>
}