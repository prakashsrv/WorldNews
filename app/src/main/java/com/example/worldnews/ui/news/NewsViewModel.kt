package com.example.worldnews.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    private val _newsState = MutableStateFlow<UiState<NewsResponse>>(UiState.Initial)

    //This is the public version that UI (Activity/Fragment/Compose) can observe, but cannot modify.
    val newsState = _newsState.asStateFlow()

    init {
            getNews()
    }

    fun getNews() {
        viewModelScope.launch {
            _newsState.value = UiState.Loading

            try {

                val response = repository.getNews()

                _newsState.value = UiState.Success(response)

            } catch (e: Exception) {
                _newsState.value = UiState.Error(e.toString())
            }
        }

    }


}