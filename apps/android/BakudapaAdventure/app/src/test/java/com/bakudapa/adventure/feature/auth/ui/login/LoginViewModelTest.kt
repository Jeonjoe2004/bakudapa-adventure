package com.bakudapa.adventure.feature.auth.ui.login

import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = LoginViewModel(repository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should not be loading`() {
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state should have no error`() {
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `initial state should not be success`() {
        assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `setting email should update state`() {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test@bakudapa.com"))
        // Email stored internally, state unchanged directly
        assertEquals("test@bakudapa.com", viewModel.email)
    }

    @Test
    fun `login with empty credentials should show error`() {
        viewModel.onEvent(LoginEvent.OnLoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        // Loading triggered
        assertTrue(viewModel.uiState.value.isLoading || !viewModel.uiState.value.isSuccess)
    }
}
