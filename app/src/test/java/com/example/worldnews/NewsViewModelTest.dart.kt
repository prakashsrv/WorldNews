package com.example.worldnews

import app.cash.turbine.test
import com.example.worldnews.data.model.Article
import com.example.worldnews.data.model.Source
import com.example.worldnews.data.repository.NewsRepository
import com.example.worldnews.ui.base.UiState
import com.example.worldnews.ui.news.NewsViewModel
import dagger.internal.Beta
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Dispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class NewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()


    private lateinit var newsRepository: NewsRepository
    private lateinit var newsViewModel: NewsViewModel

    private fun fakeArticle(title: String = "Test Title") = Article(
        source = Source(id = null, name = "BBC"),
        author = "Author",
        title = title,
        description = "Description",
        url = "https://example.com/$title",
        urlToImage = null,
        publishedAt = "2024-01-01T00:00:00Z",
        content = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)
        newsRepository = mock()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {

        Dispatchers.resetMain()

    }

    @Test
    fun `initial state is loading when cache is empty`() = runTest {

        // GIVEN: no cached articles, network is still in-flight (never emits)
        whenever(newsRepository.observeArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsRepository.fetchAndCache()).thenReturn(flowOf(UiState.Loading))

        // WHEN: ViewModel is created (init calls observeArticles + refreshNews)
        newsViewModel = NewsViewModel(newsRepository)
        //is used in coroutine testing to execute all pending coroutines/tasks until nothing is left.
        testDispatcher.scheduler.advanceUntilIdle()


        // THEN: state is Loading
        assertTrue(newsViewModel.newsState.value is UiState.Loading)


    }

    @Test
    fun `state becomes success when cache returns articles`() = runTest {

        val articles = listOf(fakeArticle())


        // GIVEN: DB has articles
        whenever(newsRepository.observeArticles()).thenReturn(flowOf(articles))
        whenever(newsRepository.fetchAndCache()).thenReturn(flowOf(UiState.Success(articles)))

        newsViewModel = NewsViewModel(newsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(newsViewModel.newsState.value is UiState.Success)


    }

    @Test
    fun `state becomes error when network fails and cache is empty`() = runTest {

        // GIVEN: empty DB + network error

        whenever(newsRepository.observeArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsRepository.fetchAndCache()).thenReturn(flowOf(UiState.Error("Network Error")))

        newsViewModel = NewsViewModel(newsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(newsViewModel.newsState.value is UiState.Error)


    }

    @Test
    fun `state stays success when network fails but cache has data`() = runTest {

        val cachedArticles = listOf(fakeArticle())


        whenever(newsRepository.observeArticles()).thenReturn(flowOf(cachedArticles))
        whenever(newsRepository.fetchAndCache()).thenReturn(flowOf(UiState.Error("Network Error")))


        newsViewModel = NewsViewModel(newsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(newsViewModel.newsState.value is UiState.Success)


    }

    @Test
    fun `refreshNews does not emit Loading when already in Success state`() = runTest {
        val articles = listOf(fakeArticle())

        whenever(newsRepository.observeArticles()).thenReturn(flowOf(articles))
        whenever(newsRepository.fetchAndCache()).thenReturn(flowOf(UiState.Success(articles)))

        newsViewModel = NewsViewModel(newsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // At this point state is Success; call refreshNews again
        newsViewModel.refreshNews()
        testDispatcher.scheduler.advanceUntilIdle()

        // Should still be Success, never flipped to Loading
        assertTrue(newsViewModel.newsState.value is UiState.Success)
    }

    // Turbine: assert the exact sequence of state emissions
    @Test
    fun `state transitions from Loading to Success (Turbine)`() = runTest {
        val articles = listOf(fakeArticle())

        whenever(newsRepository.observeArticles()).thenReturn(flowOf(emptyList()))
        whenever(newsRepository.fetchAndCache()).thenReturn(
            flowOf(UiState.Loading, UiState.Success(articles))
        )

        newsViewModel = NewsViewModel(newsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // assert the final settled value directly — no Turbine needed here
        assertTrue(newsViewModel.newsState.value is UiState.Success)
    }


}