package com.example.arthub.api

import android.content.Context
import art.example.api.service.FolderApiService
import art.example.api.service.PostApiService
import art.example.api.service.TagApiService
import art.example.api.service.UserApiService
import art.example.modules.LocalDateTimeAdapter
import com.example.art.R
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    private lateinit var retrofit: Retrofit



    // Initialize the Retrofit instance with context
    fun initialize(context: Context) {
        val baseUrl = context.getString(R.string.api_url)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("auth_token", null)

                val requestBuilder = chain.request().newBuilder()
                if (token != null && !isAuthEndpoint(chain.request().url.toString())) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                if (chain.request().method == "POST") {
                    requestBuilder.addHeader("Content-Type", "application/json")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun isAuthEndpoint(url: String): Boolean {
        return url.contains("login", ignoreCase = true)
                || url.contains("signup", ignoreCase = true)
                || url.contains("reset-password", ignoreCase = true)
                || url.contains("/reset-password-request", ignoreCase = true)
    }

    fun getPostApiService(): PostApiService = retrofit.create(PostApiService::class.java)
    fun getTagApiService(): TagApiService = retrofit.create(TagApiService::class.java)
    fun getUserApiService(): UserApiService = retrofit.create(UserApiService::class.java)
    fun getFolderApiService(): FolderApiService = retrofit.create(FolderApiService::class.java)
}
