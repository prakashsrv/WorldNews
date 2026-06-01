package com.example.worldnews.domain.usecase

import com.example.worldnews.domain.model.Article
import com.example.worldnews.domain.repository.NewsRepository
import com.example.worldnews.ui.base.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// GetNewsUseCase - fetches articles and caches them
class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<UiState<List<Article>>> = repository.fetchAndCache()
}

// ObserveNewsUseCase - observes cached articles
class ObserveNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.observeArticles()
}

// You can combine these into a single class if you prefer
class NewsUseCases @Inject constructor(
    val getNews: GetNewsUseCase,
    val observeNews: ObserveNewsUseCase
)