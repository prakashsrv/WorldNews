package com.example.worldnews.data.mapper

import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.model.Source
import com.example.worldnews.data.model.Article as ApiArticle
import com.example.worldnews.domain.model.Article as DomainArticle

// API Model → Domain Model
fun ApiArticle.toDomainModel(): DomainArticle = DomainArticle(
    source = Source(id = source.id, name = source.name),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

// Domain Model → Entity (Database)
fun DomainArticle.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    source = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

// Entity → Domain Model
fun ArticleEntity.toDomainModel(): DomainArticle = DomainArticle(
    source = Source(id = null, name = source),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

// API Model → Entity (Direct, skipping domain)
fun ApiArticle.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    source = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)