package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Link
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

open class LinkDataRepository @Inject constructor(
    private val linkDao: LinkDao,
) {
    open fun getAllLinks(): Flow<List<Link>> = linkDao.getAllLinks()

    open fun getVisitedLinks(): Flow<List<Link>> = linkDao.getVisitedLinks()

    open fun getUnvisitedLinks(): Flow<List<Link>> = linkDao.getUnvisitedLinks()

    open fun getLinksByCategory(categoryId: String): Flow<List<Link>> = linkDao.getAllLinks().map { links ->
        links.filter { link -> link.categoryIds.contains(categoryId) }
    }

    open fun getLinksByCategories(categoryIds: List<String>): Flow<List<Link>> = linkDao.getAllLinks().map { links ->
        links.filter { link ->
            link.categoryIds.any { categoryId -> categoryIds.contains(categoryId) }
        }
    }

    open suspend fun getLinkByUri(uri: URI): Link? = linkDao.getLinkByUri(uri)

    open suspend fun insertLink(link: Link) = linkDao.insertLink(link)

    open suspend fun markLinkAsVisited(link: Link) {
        val updatedLink = link.copy(visited = true)
        linkDao.updateLink(updatedLink)
    }

    open suspend fun addCategoryToLink(link: Link, categoryId: String) {
        if (!link.categoryIds.contains(categoryId)) {
            val updatedLink = link.copy(
                categoryIds = link.categoryIds + categoryId,
                updated = java.time.Instant.now(),
            )
            linkDao.updateLink(updatedLink)
        }
    }

    open suspend fun removeCategoryFromLink(link: Link, categoryId: String) {
        if (link.categoryIds.contains(categoryId)) {
            val updatedLink = link.copy(
                categoryIds = link.categoryIds.filter { it != categoryId },
                updated = java.time.Instant.now(),
            )
            linkDao.updateLink(updatedLink)
        }
    }

    open suspend fun updateLinkCategories(link: Link, categoryIds: List<String>) {
        val updatedLink = link.copy(
            categoryIds = categoryIds,
            updated = java.time.Instant.now(),
        )
        linkDao.updateLink(updatedLink)
    }

    open suspend fun updateLink(link: Link) = linkDao.updateLink(link)

    open suspend fun deleteLink(id: String) = linkDao.deleteLinkById(id)
}
