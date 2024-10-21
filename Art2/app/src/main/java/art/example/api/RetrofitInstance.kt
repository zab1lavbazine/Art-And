package com.example.arthub.api

import android.annotation.SuppressLint
import android.content.Context
import art.example.api.service.PostApiService
import art.example.api.service.TagApiService
import art.example.api.service.UserApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("StaticFieldLeak")
object RetrofitInstance {

    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
    }

    private const val BASE_URL: String = "http://arthub-backend-git-kindiole-dev.apps.sandbox-m3.1530.p1.openshiftapps.com"

    // localhost
//    private const val BASE_URL: String = "http://localhost:8080"

    // Create OkHttpClient with the interceptor
    private fun getOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val requestBuilder = chain.request().newBuilder()
                // Retrieve the token from SharedPreferences
                val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("auth_token", null)

                // Add the token to the request header if it exists
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }



    // Create Retrofit instance with the OkHttpClient
    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getPostApiService(): PostApiService {
        return getRetrofitInstance().create(PostApiService::class.java)
    }

    fun getTagApiService(): TagApiService {
        return getRetrofitInstance().create(TagApiService::class.java)
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
