package com.smartsociety.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with correct admin credentials success`() = runTest {
        viewModel.login("admin@smartsociety.com", "password")
        
        // Wait for the simulated delay in ViewModel
        testDispatcher.scheduler.advanceTimeBy(2000)
        
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Authenticated)
        assertTrue((state as AuthState.Authenticated).user.email == "admin@smartsociety.com")
    }

    @Test
    fun `login with incorrect credentials fails`() = runTest {
        viewModel.login("user@wrong.com", "wrong")
        
        testDispatcher.scheduler.advanceTimeBy(2000)
        
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Error)
        assertTrue((state as AuthState.Error).message == "Invalid credentials")
    }

    @Test
    fun `register success`() = runTest {
        viewModel.register("Test User", "test@test.com", "password")
        
        testDispatcher.scheduler.advanceTimeBy(2000)
        
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Authenticated)
        assertTrue((state as AuthState.Authenticated).user.name == "Test User")
    }
}
