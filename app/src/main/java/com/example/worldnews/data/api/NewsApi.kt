package com.example.worldnews.data.api

import com.example.worldnews.data.model.NewsResponse
import retrofit2.http.Query

interface NewsApi {

    suspend fun getNews(@Query("country") country:String = "us",@Query("apiKey") apiKey:String ): NewsResponse



}