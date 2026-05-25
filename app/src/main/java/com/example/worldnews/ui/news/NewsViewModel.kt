package com.example.worldnews.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.NewsResponse
import com.example.worldnews.data.repository.NewsRepository
import com.example.worldnews.ui.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {


    private val _newsState = MutableStateFlow<UiState<List<Article>>>(UiState.Initial)

    //This is the public version that UI (Activity/Fragment/Compose) can observe, but cannot modify.
    val newsState = _newsState.asStateFlow()

    init {
        observeArticles()
        refreshNews()
    }


    fun observeArticles() {
        viewModelScope.launch {
            repository.observeArticles().collect { articles ->
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
                repository.fetchAndCache()
            } catch (e: Exception) {
                if (_newsState.value !is UiState.Success) {
                    _newsState.value = UiState.Error(e.message)
                }
            }
        }
    }


}