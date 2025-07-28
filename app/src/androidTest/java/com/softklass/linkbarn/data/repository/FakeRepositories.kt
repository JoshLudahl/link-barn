package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.CategoryDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Link
import java.net.URI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.mockito.Mockito.mock

/**
 * Fake implementation of LinkDataRepository for testing.
 */
class FakeLinkDataRepository : LinkDataRepository(mock(LinkDao::class.java)) {
    private val links = mutableListOf<Link>()
    private var linkToReturnForUri: Link? = null

    override fun getAllLinks(): Flow<List<Link>> = flowOf(links)

    override fun getVisitedLinks(): Flow<List<Link>> = flowOf(links.filter { it.visited })

    override fun getUnvisitedLinks(): Flow<List<Link>> = flowOf(links.filter { !it.visited })

    override fun getLinksByCategories(categoryIds: List<String>): Flow<List<Link>> = flowOf(links.filter { link -> link.categoryIds.any { categoryIds.contains(it) } })

    override suspend fun getLinkByUri(uri: URI): Link? = linkToReturnForUri

    fun setLinkToReturnForUri(link: Link?) {
        linkToReturnForUri = link
    }

    // Override other methods with empty implementations
    override suspend fun insertLink(link: Link) {}
    override suspend fun markLinkAsVisited(link: Link) {}
    override suspend fun addCategoryToLink(link: Link, categoryId: String) {}
    override suspend fun removeCategoryFromLink(link: Link, categoryId: String) {}
    override suspend fun updateLinkCategories(link: Link, categoryIds: List<String>) {}
    override suspend fun updateLink(link: Link) {}
    override suspend fun deleteLink(id: String) {}
}

/**
 * Fake implementation of CategoryRepository for testing.
 */
class FakeCategoryRepository : CategoryRepository(mock(CategoryDao::class.java)) {
    private val categories = mutableListOf<Category>()

    override fun getAllCategories(): Flow<List<Category>> = flowOf(categories)

    override suspend fun getCategoryById(id: String): Category? = categories.find { it.id == id }

    override suspend fun getCategoryByName(name: String): Category? = categories.find { it.name == name }

    // Override other methods with empty implementations
    override suspend fun insertCategory(category: Category): Long = 0
    override suspend fun getOrCreateCategory(name: String): Category = Category(name = name)
    override suspend fun updateCategory(category: Category) {}
    override suspend fun deleteCategory(category: Category) {}
    override suspend fun deleteCategoryById(id: String) {}
}
