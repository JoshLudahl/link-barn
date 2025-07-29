package com.softklass.linkbarn.ui.categories

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.repository.CategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CategoriesViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val categoryRepository: CategoryRepository = mock()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CategoriesViewModel

    @Before
    fun setup() {
        whenever(categoryRepository.getAllCategories()).thenReturn(flowOf(emptyList()))
        viewModel = CategoriesViewModel(
            categoryRepository = categoryRepository,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `deleteCategory should call repository deleteCategory with correct category`() = runTest {
        // Given
        val testCategory = Category(
            id = "test-id",
            name = "Test Category",
        )

        // When
        viewModel.deleteCategory(testCategory)

        // Then
        verify(categoryRepository).deleteCategory(testCategory)
    }

    @Test
    fun `addCategory should create category with unique id`() = runTest {
        // Given
        val categoryName = "New Category"
        whenever(categoryRepository.getCategoryByName(categoryName)).thenReturn(null)

        // When
        viewModel.addCategory(categoryName)

        // Then
        verify(categoryRepository).insertCategory(org.mockito.kotlin.any())
    }
}
