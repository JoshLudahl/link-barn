package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.data.repository.LinkDataRepository
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class MainViewModelTest {
    private val linkDataRepository: LinkDataRepository = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(linkDataRepository)
    }

    @Test
    fun `getAllLinks should call repository`() {
        // Given
        println("[DEBUG_LOG] Starting getAllLinks test")

        // When
        //viewModel.getAllLinks()

        // Then
        verify(linkDataRepository).getAllLinks()
        println("[DEBUG_LOG] Test completed successfully")
    }
}
