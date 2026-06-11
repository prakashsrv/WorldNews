package com.example.worldnews

import androidx.compose.ui.unit.Constraints
import app.cash.turbine.test
import com.example.worldnews.data.api.NewsApi
import com.example.worldnews.data.local.ArticleDao
import com.example.worldnews.data.local.ArticleEntity
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.NewsResponse
import com.example.worldnews.data.model.Source
import com.example.worldnews.data.repository.NewsRepository
import com.example.worldnews.ui.base.UiState
import com.example.worldnews.utils.Constants
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NewsRepositoryTestPractice {


    private lateinit var newsApi: NewsApi

    private lateinit var newsDao: ArticleDao

    private lateinit var newsRepository: NewsRepository

    private fun fakeEntity(url: String = "https://example.com/1") = ArticleEntity(
        url = url,
        source = "BBC",
        author = "Author",
        title = "Test Title",
        description = "Test Description",
        urlToImage = null,
        publishedAt = "2024-01-01T00:00:00Z",
        content = null
    )

    private fun fakeArticle(url: String = "https://example.com/1") = Article(
        source = Source(id = null, name = "BBC"),
        author = "Author",
        title = "Test Title",
        description = "Test Description",
        url = url,
        urlToImage = null,
        publishedAt = "2024-01-01T00:00:00Z",
        content = null
    )

    private fun fakeNewsRepository(vararg articles: Article) =
        NewsResponse(status = "ok", totalResults = articles.size, articles = articles.toList())


    @Before
    fun setup() {

        newsApi = mock()

        newsDao = mock()

        newsRepository = NewsRepository(newsApi, newsDao)

    }

    @Test
    fun `fetch and cache emits success when cache is not empty`() {

        val cache = listOf(fakeEntity());

        whenever(newsDao.getAllArticles())
            .thenReturn(flowOf(cache))


        whenever(newsApi.getNews(country = "US", apiKey = Constants.API_KEY)).thenReturn(fakeNewsRepository(fakeArticle()))

        newsRepository.fetchAndCache().test {

            //first emitted item from floew
            val first = awaitItem()

            //verify repository emits success data
            assertTrue("Expected success trom cache",first is UiState.Success)

            // Verify cached data contains exactly 1 article
            assertEquals(1,(first as UiState.Success).data.size)

            // Verify Flow completed successfully
            awaitComplete()




        }
    }


    @Test
    fun `fetch and cache emits loading when cache is empty `(){

        whenever(newsDao.getAllArticles()).thenReturn(flowOf(emptyList()))

        whenever(newsApi.getNews(country = "us", Constants.API_KEY)).thenReturn(fakeNewsRepository(fakeArticle()))


        newsRepository.fetchAndCache().test {

            val first = awaitItem()

            assertTrue("expected loading",first is UiState.Loading)

            awaitComplete()

        }


    }


}