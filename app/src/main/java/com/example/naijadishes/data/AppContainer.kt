package com.example.naijadishes.data

import android.content.Context
import com.example.naijadishes.network.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


interface AppContainer{
    val networkRepository: NetworkRepository
    val offlineRepository: OfflineRepository
}
class DefaultAppContainer(private val context: Context): AppContainer {
    private val baseUrl = "https://b05un-naijadishes.hf.space/"
    private var jwt: String? = null
    fun setToken(newToken: String) {
        jwt = newToken
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        jwt?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        chain.proceed(requestBuilder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(client)
        .build()
    private val retrofitApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    override val networkRepository: NetworkRepository by lazy {
        NetworkRepository(retrofitApiService)
    }
    override val offlineRepository: OfflineRepository by lazy{
        OfflineRepository(UserDatabase.getDatabase(context).userDao())
    }
}