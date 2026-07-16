package com.bakudapa.adventure.feature.auth.ui.login

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.usecase.SignInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var signInUseCase: SignInUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        signInUseCase = mock()
        viewModel = LoginViewModel(signInUseCase)
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
        assertNull(viewModel.uiState.value.generalError)
    }

    @Test
    fun `setting email should update state`() {
        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        assertEquals("test@bakudapa.com", viewModel.uiState.value.email)
    }

    @Test
    fun `setting password should update state`() {
        viewModel.onEvent(LoginEvent.PasswordChanged("secret123"))
        assertEquals("secret123", viewModel.uiState.value.password)
    }

    @Test
    fun `login with empty email should show email error`() {
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with empty password should show password error`() {
        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with short password should show password error`() {
        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.PasswordChanged("ab"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with invalid email format should show email error`() {
        viewModel.onEvent(LoginEvent.EmailChanged("notanemail"))
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login with valid credentials should call use case`() = runTest {
        whenever(signInUseCase("test@bakudapa.com", "validpass1")).thenReturn(DataResult.Success(Unit))

        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(signInUseCase).invoke("test@bakudapa.com", "validpass1")
    }

    @Test
    fun `login success should emit NavigateToHome effect`() = runTest {
        whenever(signInUseCase("test@bakudapa.com", "validpass1")).thenReturn(DataResult.Success(Unit))

        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val effect = viewModel.effect.first()
        assertTrue(effect is LoginEffect.NavigateToHome)
    }

    @Test
    fun `login failure should emit ShowError effect`() = runTest {
        val errorMsg = "Invalid credentials"
        whenever(signInUseCase("test@bakudapa.com", "validpass1"))
            .thenReturn(DataResult.Error(Exception(errorMsg)))

        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        val effect = viewModel.effect.first()
        assertTrue(effect is LoginEffect.ShowError)
        assertEquals(errorMsg, (effect as LoginEffect.ShowError).message)
    }

    @Test
    fun `login failure should set general error`() {
        whenever(signInUseCase("test@bakudapa.com", "validpass1"))
            .thenReturn(DataResult.Error(Exception("Boom!")))

        viewModel.onEvent(LoginEvent.EmailChanged("test@bakudapa.com"))
        viewModel.onEvent(LoginEvent.PasswordChanged("validpass1"))
        viewModel.onEvent(LoginEvent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.generalError)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
