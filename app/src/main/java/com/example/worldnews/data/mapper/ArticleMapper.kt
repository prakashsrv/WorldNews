package com.example.worldnews.data.mapper

import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.Source

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    source = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

fun ArticleEntity.toDomainModel(): Article = Article(
    source = Source(id = null, name = source),  // ← wrap the string back into Source
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)