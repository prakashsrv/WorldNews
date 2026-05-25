package com.example.worldnews.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.worldnews.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * From articles ORDER BY publishedAt DESC" )
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()



}