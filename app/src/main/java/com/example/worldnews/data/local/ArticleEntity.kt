package com.example.worldnews.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.worldnews.data.model.Source

@Entity(tableName = "articles")
data class ArticleEntity (

    @PrimaryKey val url: String,
    val source: String,
    val author: String? = null,
    val title: String,
    val description: String? = null,
    val urlToImage: String? = null,
    val publishedAt: String,
    val content: String? = null


)
