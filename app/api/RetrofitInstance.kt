package com.example.arthub.api

import com.example.arthub.BuildConfig
import com.example.arthub.api.service.PostApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL = BuildConfig.BASE_URL

    private val retroit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val postApi: PostApiService by lazy {
        retroit.create(PostApiService::class.java)
    }
}