package com.example.worldnews.data.repository

import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.mapper.toDomainModel
import com.example.worldnews.data.mapper.toEntity
import com.example.worldnews.data.model.Article
import com.example.worldnews.ui.base.UiState
import com.example.worldnews.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: ArticleDao
) {


    fun fetchAndCache(): Flow<UiState<List<Article>>> = flow {
        //emit db data first
        val cached = newsDao.getAllArticles().first()
        if (cached.isNotEmpty()) {
            emit(UiState.Success(cached.map {
                it.toDomainModel()
            }))
        } else {
            emit(UiState.Loading)
        }

        //try the network

        try {
            val response = newsApi.getNews("us", Constants.API_KEY)
            newsDao.clearAll()
            newsDao.upsertArticles(response.articles.map {
                it.toEntity()
            })
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(UiState.Error(e.message))
            }
        }


    }

    fun observeArticles(): Flow<List<Article>> = newsDao.getAllArticles().map { listOfEntities ->
        // one full list arriving from DB
        listOfEntities.map { entity -> // each individual row in that list
            entity.toDomainModel()     // convert that one row to Article
        }
    }

    }