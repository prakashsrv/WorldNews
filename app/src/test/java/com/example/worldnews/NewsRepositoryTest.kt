package com.example.worldnews

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
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for NewsRepository.
 *
 * Key concepts demonstrated:
 * - Testing a cold Flow produced by `flow { }` builder
 * - Verifying DAO interactions (clearAll, upsert) happen correctly
 * - Testing offline-first caching logic
 */

class NewsRepositoryTest {

    private lateinit var newsApi: NewsApi
    private lateinit var articleDao: ArticleDao
    private lateinit var repository: NewsRepository

    //helpers
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
        articleDao = mock()
        repository = NewsRepository(newsApi, articleDao)

    }

    @Test
    fun `fetchAndCache emits Success immediately when cache is not empty`() = runTest {

        // Fake cached data already present in local database
        val cache = listOf(fakeEntity())

        // Mock Room database response
        whenever(articleDao.getAllArticles())
            .thenReturn(flowOf(cache))

        // Mock backend API response
        whenever(newsApi.getNews("us", Constants.API_KEY))
            .thenReturn(fakeNewsRepository(fakeArticle()))

        // Act + Assert

        repository.fetchAndCache().test {

            // First emitted item from Flow
            val first = awaitItem()

            // Verify repository emits Success state
            assertTrue(
                "Expected success from cache",
                first is UiState.Success
            )

            // Verify cached data contains exactly 1 article
            assertEquals(
                1,
                (first as UiState.Success).data.size
            )

            // Verify Flow completed successfully
            awaitComplete()
        }
    }

    @Test
    fun `fetchAndCache emits Loading when cache is empty`() = runTest {
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsApi.getNews("us", Constants.API_KEY))
            .thenReturn(fakeNewsRepository(fakeArticle()))

        repository.fetchAndCache().test {
            val first = awaitItem()
            assertTrue("Expected Loading when cache is empty", first is UiState.Loading)
            awaitComplete()
        }
    }

    @Test
    fun `fetchAndCache calls clearAll then upsertArticles on network success`() = runTest {

        whenever(articleDao.getAllArticles())
            .thenReturn(flowOf(emptyList()))

        whenever(newsApi.getNews(any(), any()))
            .thenReturn(fakeNewsRepository(fakeArticle()))

        repository.fetchAndCache().test {
            awaitItem() // consume Loading (cache is empty)
            awaitComplete() // now the flow can complete
        }

        val inOrder = org.mockito.Mockito.inOrder(articleDao)
        inOrder.verify(articleDao).clearAll()
        inOrder.verify(articleDao).upsertArticles(any())
    }

    @Test
    fun `fetchAndCache emits Error when network fails and cache is empty`() = runTest {
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsApi.getNews("us", Constants.API_KEY))
            .thenThrow(RuntimeException("No internet"))

        repository.fetchAndCache().test {
            awaitItem() // UiState.Loading (cache empty)
            val second = awaitItem()
            assertTrue("Expected Error after network failure", second is UiState.Error)
            assertEquals("No internet", (second as UiState.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `fetchAndCache does NOT emit Error when network fails but cache exists`() = runTest {
        // The repository intentionally suppresses the error if there was cached data
        val cached = listOf(fakeEntity())
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(cached))
        whenever(newsApi.getNews("us", Constants.API_KEY))
            .thenThrow(RuntimeException("Timeout"))

        val emissions = mutableListOf<UiState<*>>()
        repository.fetchAndCache().test {
            emissions.add(awaitItem())
            awaitComplete()
        }

        // Only the cache-hit Success is emitted, no Error follows
        assertTrue(emissions.all { it is UiState.Success })
    }

    @Test
    fun `fetchAndCache does NOT call clearAll when network call fails`() = runTest {
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsApi.getNews("us", Constants.API_KEY))
            .thenThrow(RuntimeException("Server error"))

        repository.fetchAndCache().test {
            awaitItem() // Loading
            awaitItem() // Error
            awaitComplete()
        }

        // If network fails, we should NOT wipe the DB
        verify(articleDao, never()).clearAll()
    }

    @Test
    fun `observeArticles maps entities to domain models`() = runTest {
        val entity = fakeEntity("https://bbc.com/1")
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(listOf(entity)))

        repository.observeArticles().test {
            val articles = awaitItem()
            assertEquals(1, articles.size)
            assertEquals("https://bbc.com/1", articles[0].url)
            assertEquals("BBC", articles[0].source.name)
            awaitComplete()
        }
    }

    @Test
    fun `observeArticles emits empty list when DAO is empty`() = runTest {
        whenever(articleDao.getAllArticles()).thenReturn(flowOf(emptyList()))

        repository.observeArticles().test {
            val articles = awaitItem()
            assertTrue(articles.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `observeArticles re-emits when DAO emits new data`() = runTest {
        val first = listOf(fakeEntity("https://example.com/1"))
        val second = listOf(fakeEntity("https://example.com/1"), fakeEntity("https://example.com/2"))

        // Simulate a DB that emits two updates (like a real Room Flow would)
        val dbFlow = kotlinx.coroutines.flow.flow {
            emit(first)
            emit(second)
        }
        whenever(articleDao.getAllArticles()).thenReturn(dbFlow)

        repository.observeArticles().test {
            assertEquals(1, awaitItem().size)
            assertEquals(2, awaitItem().size)
            awaitComplete()
        }
    }


}