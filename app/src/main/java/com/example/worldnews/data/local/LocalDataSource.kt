package com.example.worldnews.data.datasource.local

import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.ArticleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Abstract local data source
interface LocalDataSource {
    fun getAllArticles(): Flow<List<ArticleEntity>>
    suspend fun upsertArticles(articles: List<ArticleEntity>)
    suspend fun clearAll()
}

// Implementation
class LocalDataSourceImpl @Inject constructor(
    private val articleDao: ArticleDao
) : LocalDataSource {

    override fun getAllArticles(): Flow<List<ArticleEntity>> = articleDao.getAllArticles()

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        articleDao.upsertArticles(articles)
    }

    override suspend fun clearAll() {
        articleDao.clearAll()
    }
}