package com.bakudapa.adventure.feature.home.ui

import com.bakudapa.adventure.feature.home.domain.usecase.GetHomeDataUseCase
import com.bakudapa.adventure.feature.home.domain.usecase.SearchMountainsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

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
        viewModel.onEvent(HomeEvent.OnSearchQueryChanged(""))
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `mountain click should send navigation effect`() {
        viewModel.onEvent(HomeEvent.OnMountainClicked("mount_semeru"))
        // Effect sent to channel
        assertEquals("mount_semeru", viewModel.effect.value)
    }
}
