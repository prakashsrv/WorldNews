package com.example.worldnews.data.mapper

import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.Source

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    source = source,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

fun ArticleEntity.toDomainModel(): Article = Article(
    source = source,
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)