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

/**
 * Test to verify that the category deletion bugs have been fixed:
 * 1. Pending deletions are processed when leaving screen
 * 2. Multiple swipe deletions work properly
 */
@ExperimentalCoroutinesApi
class CategoryDeletionBugFixesTest {

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
        clearInvocations(categoryRepository)
    }

    @Test
    fun `processPendingDeletions should delete all pending categories immediately`() = runTest {
        // Given - multiple categories pending deletion
        val category1 = Category(id = "1", name = "Category 1")
        val category2 = Category(id = "2", name = "Category 2")

        // When - delete multiple categories
        viewModel.deleteCategory(category1)
        runCurrent()
        viewModel.deleteCategory(category2)
        runCurrent()

        // Verify both are in pending deletions
        val pendingDeletions = viewModel.pendingDeletions.value
        assert(pendingDeletions.contains(category1.id))
        assert(pendingDeletions.contains(category2.id))

        // When - process pending deletions (simulating leaving screen)
        viewModel.processPendingDeletions()
        runCurrent()

        // Then - both categories should be deleted immediately
        verify(categoryRepository).deleteCategory(category1)
        verify(categoryRepository).deleteCategory(category2)

        // And pending deletions should be cleared
        assert(viewModel.pendingDeletions.value.isEmpty())

        println("[DEBUG_LOG] Bug 1 fixed: Pending deletions are processed when leaving screen")
    }

    @Test
    fun `multiple swipe deletions should work properly with individual undo`() = runTest {
        // Given - multiple categories
        val category1 = Category(id = "1", name = "Category 1")
        val category2 = Category(id = "2", name = "Category 2")

        // When - delete first category
        viewModel.deleteCategory(category1)
        runCurrent()

        // Verify first category is pending
        assert(viewModel.pendingDeletions.value.contains(category1.id))

        // When - delete second category
        viewModel.deleteCategory(category2)
        runCurrent()

        // Then - both categories should be in pending deletions
        val pendingDeletions = viewModel.pendingDeletions.value
        assert(pendingDeletions.contains(category1.id))
        assert(pendingDeletions.contains(category2.id))

        // When - undo the most recent deletion (category2)
        viewModel.undoDelete()
        runCurrent()

        // Then - category2 should be removed from pending, category1 should remain
        val afterUndo = viewModel.pendingDeletions.value
        assert(!afterUndo.contains(category2.id)) // category2 was undone
        assert(afterUndo.contains(category1.id)) // category1 is still pending

        // When - wait for category1 to be deleted
        advanceTimeBy(5000)
        runCurrent()

        // Then - only category1 should be deleted, category2 should not
        verify(categoryRepository).deleteCategory(category1)
        verify(categoryRepository, org.mockito.kotlin.never()).deleteCategory(category2)

        println("[DEBUG_LOG] Bug 2 fixed: Multiple swipe deletions work properly with individual undo")
    }
}
