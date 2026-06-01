package com.example.worldnews.data.datasource.remote

import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.data.model.Article
import com.example.worldnews.utils.Constants
import javax.inject.Inject

// Abstract remote data source
interface RemoteDataSource {
    suspend fun getNews(country: String): List<Article>
}

// Implementation
class RemoteDataSourceImpl @Inject constructor(
    private val newsApi: NewsApi
) : RemoteDataSource {

    override suspend fun getNews(country: String): List<Article> {
        val response = newsApi.getNews(country, Constants.API_KEY)
        return response.articles
    }
}