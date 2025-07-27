package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Link
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LinkDataRepository @Inject constructor(
    private val linkDao: LinkDao,
) {
    fun getAllLinks(): Flow<List<Link>> = linkDao.getAllLinks()

    fun getVisitedLinks(): Flow<List<Link>> = linkDao.getVisitedLinks()

    fun getUnvisitedLinks(): Flow<List<Link>> = linkDao.getUnvisitedLinks()

    suspend fun getLinkByUri(uri: URI): Link? = linkDao.getLinkByUri(uri)

    suspend fun insertLink(link: Link) = linkDao.insertLink(link)

    suspend fun markLinkAsVisited(link: Link) {
        val updatedLink = link.copy(visited = true)
        linkDao.updateLink(updatedLink)
    }

    suspend fun deleteLink(id: String) = linkDao.deleteLinkById(id)
}
