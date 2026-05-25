package com.example.worldnews.di.module

import android.content.Context
import androidx.room.Room
import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.NewsDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//“This class tells Hilt how to provide dependencies.”
@Module
@InstallIn(SingletonComponent::class)//“Where should this dependency live?”
class ApplicationModule {

    @BaseUrl
    @Provides
    fun provideBaseUrl():String = "https://newsapi.org/v2/"

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(@BaseUrl baseUrl: String,gson: Gson) : Retrofit{

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

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase{
        return Room.databaseBuilder (context, NewsDatabase::class.java, "news_db").build()

    }

    @Provides
    @Singleton
    fun provideArticleDao(db: NewsDatabase): ArticleDao = db.articleDao()












}