package com.example.worldnews.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldnews.domain.model.Article
import com.example.worldnews.domain.usecase.NewsUseCases
import com.example.worldnews.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val useCases: NewsUseCases
) : ViewModel() {

    private val _newsState = MutableStateFlow<UiState<List<Article>>>(UiState.Initial)
    val newsState = _newsState.asStateFlow()

    init {
        observeArticles()
        refreshNews()
    }

    private fun observeArticles() {
        viewModelScope.launch {
            useCases.observeNews().collect { articles ->
                if (articles.isNotEmpty()) {
                    _newsState.value = UiState.Success(articles)
                }
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            if (_newsState.value !is UiState.Success) {
                _newsState.value = UiState.Loading
            }
            try {
                val currentState = _newsState.value
                val alreadyHasData = currentState is UiState.Success

                useCases.getNews().collect { uiState ->
                    val newDataArrived = uiState is UiState.Success
                    if (newDataArrived || !alreadyHasData) {
                        _newsState.value = uiState
                    }
                }
            } catch (e: Exception) {
                if (_newsState.value !is UiState.Success) {
                    _newsState.value = UiState.Error(e.message)
                }
            }
        }
    }
}