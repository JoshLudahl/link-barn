package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.flow.Flow
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

class LinkDataRepository @Inject constructor(
    private val linkDao: LinkDao
) {
    fun getAllLinks(): Flow<List<Link>> = linkDao.getAllLinks()

    fun getLinkById(id: String): Flow<Link?> = linkDao.getLinkById(id)

    suspend fun getLinkByUri(uri: URI): Link? = linkDao.getLinkByUri(uri)

    suspend fun insertLink(link: Link) = linkDao.insertLink(link)

    suspend fun updateLink(link: Link) = linkDao.updateLink(link)

    suspend fun deleteLink(id: String) = linkDao.deleteLinkById(id)
}
