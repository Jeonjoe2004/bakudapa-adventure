package com.bakudapa.adventure.feature.home.ui

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.model.Mountain
import org.mockito.kotlin.verify
import com.bakudapa.adventure.feature.home.domain.usecase.GetHomeDataUseCase
import com.bakudapa.adventure.feature.home.domain.usecase.SearchMountainsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getHomeDataUseCase: GetHomeDataUseCase
    private lateinit var searchMountainsUseCase: SearchMountainsUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getHomeDataUseCase = mock()
        searchMountainsUseCase = mock()
        whenever(getHomeDataUseCase()).thenReturn(flowOf(DataResult.Loading))
        viewModel = HomeViewModel(getHomeDataUseCase, searchMountainsUseCase)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be loading`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `search query should update state`() {
        viewModel.onEvent(HomeEvent.OnSearchQueryChanged("semeru"))
        assertEquals("semeru", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `empty search query should clear results`() {
        viewModel.onEvent(HomeEvent.OnSearchQueryChanged("semeru"))
        viewModel.onEvent(HomeEvent.OnSearchQueryChanged(""))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `search query triggers debounce and search`() = runTest {
        whenever(searchMountainsUseCase("semeru")).thenReturn(
            DataResult.Success(listOf(Mountain("1", "Semeru", "Jawa Timur", 3676, "", 4.8f)))
        )

        viewModel.onEvent(HomeEvent.OnSearchQueryChanged("semeru"))
        testDispatcher.scheduler.advanceUntilIdle()

        verify(searchMountainsUseCase).invoke("semeru")
        assertFalse(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `mountain click should send navigation effect`() = runTest {
        viewModel.onEvent(HomeEvent.OnMountainClicked("mount_semeru"))
        testDispatcher.scheduler.advanceUntilIdle()
        val effect = viewModel.effect.first()
        assertTrue(effect is HomeEffect.NavigateToMountainDetail)
        assertEquals("mount_semeru", (effect as HomeEffect.NavigateToMountainDetail).id)
    }

    @Test
    fun `trail click should send navigation effect`() = runTest {
        viewModel.onEvent(HomeEvent.OnTrailClicked("trail_1"))
        testDispatcher.scheduler.advanceUntilIdle()
        val effect = viewModel.effect.first()
        assertTrue(effect is HomeEffect.NavigateToTrailDetail)
        assertEquals("trail_1", (effect as HomeEffect.NavigateToTrailDetail).id)
    }

    @Test
    fun `post click should send navigation effect`() = runTest {
        viewModel.onEvent(HomeEvent.OnPostClicked("post_abc"))
        testDispatcher.scheduler.advanceUntilIdle()
        val effect = viewModel.effect.first()
        assertTrue(effect is HomeEffect.NavigateToPostDetail)
        assertEquals("post_abc", (effect as HomeEffect.NavigateToPostDetail).id)
    }

    @Test
    fun `load home data error should emit ShowError`() = runTest {
        whenever(getHomeDataUseCase()).thenReturn(
            flowOf(DataResult.Error(Exception("Network error")))
        )
        val vm = HomeViewModel(getHomeDataUseCase, searchMountainsUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val effect = vm.effect.first()
        assertTrue(effect is HomeEffect.ShowError)
    }
}
