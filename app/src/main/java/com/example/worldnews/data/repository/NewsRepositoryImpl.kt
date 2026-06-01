package com.example.worldnews.data.repository


import com.example.worldnews.data.datasource.local.LocalDataSource
import com.example.worldnews.data.datasource.remote.RemoteDataSource
import com.example.worldnews.data.mapper.toDomainModel
import com.example.worldnews.data.mapper.toEntity
import com.example.worldnews.domain.model.Article
import com.example.worldnews.domain.repository.NewsRepository
import com.example.worldnews.ui.base.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : NewsRepository {

    override fun fetchAndCache(): Flow<UiState<List<Article>>> = flow {
        // Emit cached data first
        val cached = localDataSource.getAllArticles().first()
        if (cached.isNotEmpty()) {
            emit(UiState.Success(cached.map { it.toDomainModel() }))
        } else {
            emit(UiState.Loading)
        }

        // Try the network
        try {
            val remoteArticles = remoteDataSource.getNews("us")
            localDataSource.clearAll()
            localDataSource.upsertArticles(remoteArticles.map { it.toEntity() })
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(UiState.Error(e.message))
            }
        }
    }

    override fun observeArticles(): Flow<List<Article>> =
        localDataSource.getAllArticles().map { entities ->
            entities.map { it.toDomainModel() }
        }
}