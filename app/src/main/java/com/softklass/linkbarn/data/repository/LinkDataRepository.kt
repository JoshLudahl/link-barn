package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Link
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LinkDataRepository @Inject constructor(
    private val linkDao: LinkDao,
) {
    fun getAllLinks(): Flow<List<Link>> = linkDao.getAllLinks()

    fun getVisitedLinks(): Flow<List<Link>> = linkDao.getVisitedLinks()

    fun getUnvisitedLinks(): Flow<List<Link>> = linkDao.getUnvisitedLinks()

    fun getLinksByCategory(categoryId: String): Flow<List<Link>> = linkDao.getAllLinks().map { links ->
        links.filter { link -> link.categoryIds.contains(categoryId) }
    }

    suspend fun getLinkByUri(uri: URI): Link? = linkDao.getLinkByUri(uri)

    suspend fun insertLink(link: Link) = linkDao.insertLink(link)

    suspend fun markLinkAsVisited(link: Link) {
        val updatedLink = link.copy(visited = true)
        linkDao.updateLink(updatedLink)
    }

    suspend fun addCategoryToLink(link: Link, categoryId: String) {
        if (!link.categoryIds.contains(categoryId)) {
            val updatedLink = link.copy(
                categoryIds = link.categoryIds + categoryId,
                updated = java.time.Instant.now(),
            )
            linkDao.updateLink(updatedLink)
        }
    }

    suspend fun removeCategoryFromLink(link: Link, categoryId: String) {
        if (link.categoryIds.contains(categoryId)) {
            val updatedLink = link.copy(
                categoryIds = link.categoryIds.filter { it != categoryId },
                updated = java.time.Instant.now(),
            )
            linkDao.updateLink(updatedLink)
        }
    }

    suspend fun updateLinkCategories(link: Link, categoryIds: List<String>) {
        val updatedLink = link.copy(
            categoryIds = categoryIds,
            updated = java.time.Instant.now(),
        )
        linkDao.updateLink(updatedLink)
    }

    suspend fun updateLink(link: Link) = linkDao.updateLink(link)

    suspend fun deleteLink(id: String) = linkDao.deleteLinkById(id)
}
