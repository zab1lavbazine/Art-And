package com.example.arthub.api

import art.example.api.service.PostApiService
import art.example.api.service.UserApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://your-api-url.com/"

    // Create OkHttpClient with the interceptor
    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val originalRequest: Request = chain.request()
                chain.proceed(originalRequest) // Just proceed without adding the token here
            }
            .build()
    }

    // Create Retrofit instance with the OkHttpClient
    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getPostApiService(): PostApiService {
        return getRetrofitInstance().create(PostApiService::class.java)
    }

    fun getUserApiService(): UserApiService {
        return getRetrofitInstance().create(UserApiService::class.java)
    }

    // Method to create an authenticated request
    fun createAuthenticatedRequest(authToken: String?): Request {
        val builder = Request.Builder()
        if (!authToken.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $authToken")
        }
        return builder.build()
    }
}
