package com.softklass.linkbarn.ui.categories

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.model.Category
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
    fun `deleteCategory should show snackbar and delete after delay`() = runTest {
        // Given
        val testCategory = Category(
            id = "test-id",
            name = "Test Category",
        )

        // When
        viewModel.deleteCategory(testCategory)
        advanceTimeBy(100) // Advance time to ensure coroutine starts
        runCurrent() // Execute the immediate part of deleteCategory

        // Then - verify snackbar is shown immediately
        val snackbarState = viewModel.snackbarState.value
        assert(snackbarState is SnackbarState.Visible)
        assert((snackbarState as SnackbarState.Visible).message == "Category deleted")
        assert(snackbarState.categoryName == "Test Category")

        // Advance time by 5 seconds to trigger the deletion
        advanceTimeBy(5000)
        runCurrent() // Execute the delayed part

        // Then - verify repository deleteCategory is called after delay
        verify(categoryRepository).deleteCategory(testCategory)
    }

    @Test
    fun `undoDelete should cancel deletion and hide snackbar`() = runTest {
        // Given
        val testCategory = Category(
            id = "test-id",
            name = "Test Category",
        )

        // When - delete category and then undo
        viewModel.deleteCategory(testCategory)
        runCurrent() // Execute the immediate part of deleteCategory

        // Verify snackbar is shown
        assert(viewModel.snackbarState.value is SnackbarState.Visible)

        // Undo the deletion
        viewModel.undoDelete()

        // Then - verify snackbar is hidden
        assert(viewModel.snackbarState.value is SnackbarState.Hidden)

        // Advance time to ensure deletion doesn't happen
        advanceTimeBy(5000)
        runCurrent() // Execute any remaining coroutines

        // Verify repository deleteCategory is never called
        verify(categoryRepository, org.mockito.kotlin.never()).deleteCategory(testCategory)
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
