package com.example.worldnews.di.module

import android.content.Context
import androidx.room.Room
import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.data.datasource.local.LocalDataSource
import com.example.worldnews.data.datasource.local.LocalDataSourceImpl
import com.example.worldnews.data.datasource.remote.RemoteDataSource
import com.example.worldnews.data.datasource.remote.RemoteDataSourceImpl
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.NewsDatabase
import com.example.worldnews.data.repository.NewsRepositoryImpl
import com.example.worldnews.domain.repository.NewsRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    // ===== Network & Serialization =====

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = "https://newsapi.org/v2/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(@BaseUrl baseUrl: String, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    // ===== Database =====

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return Room.databaseBuilder(context, NewsDatabase::class.java, "news_db").build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(db: NewsDatabase): ArticleDao = db.articleDao()
}

// Abstract module for bindings (interfaces → implementations)
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Data Source Bindings
    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(impl: LocalDataSourceImpl): LocalDataSource

    // Repository Binding
    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository
}