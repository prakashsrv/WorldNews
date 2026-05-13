package com.example.worldnews.data.repository

import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.utils.Constants
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi
) {

    suspend fun getNews() = newsApi.getNews("us", Constants.API_KEY);
}