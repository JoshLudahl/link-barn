package com.softklass.linkbarn.ui.categories

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CategoriesViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val categoryRepository: CategoryRepository = mock()
    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setup() {
        whenever(categoryRepository.getAllCategories()).thenReturn(flowOf(emptyList()))
        viewModel = CategoriesViewModel(
            categoryRepository = categoryRepository,
            dispatcher = Dispatchers.Main,
        )
        // Clear invocations from constructor call to getAllCategories()
        clearInvocations(categoryRepository)
    }

    @Test
    fun `addCategory should create category with unique id`() = runTest {
        // Given
        val categoryName = "New Category"
        whenever(categoryRepository.getCategoryByName(categoryName)).thenReturn(null)

        // When
        viewModel.addCategory(categoryName)
        advanceTimeBy(100) // Advance time to ensure coroutine executes
        runCurrent() // Execute the coroutine

        // Then
        verify(categoryRepository).insertCategory(org.mockito.kotlin.any())
    }
}
