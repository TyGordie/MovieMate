package com.example.moviemate.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // For a physical phone, use the computer's current Wi-Fi IPv4 address and keep the API server running.
    // For a hosted API, replace this with its HTTPS base URL.
    private const val BASE_URL = "http://192.168.10.179:3000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
