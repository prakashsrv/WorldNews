package com.example.worldnews.domain.repository

import com.example.worldnews.domain.model.Article
import com.example.worldnews.ui.base.UiState
import kotlinx.coroutines.flow.Flow

// Lives in domain layer - data layer depends on this, not the other way around
interface NewsRepository {

    // Fetch from network and cache locally
    fun fetchAndCache(): Flow<UiState<List<Article>>>

    // Observe cached articles
    fun observeArticles(): Flow<List<Article>>
}