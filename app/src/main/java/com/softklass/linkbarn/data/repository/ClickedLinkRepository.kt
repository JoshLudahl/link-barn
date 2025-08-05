package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.ClickedLinkDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.ClickedLink
import com.softklass.linkbarn.data.model.Link
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

open class ClickedLinkRepository @Inject constructor(
    private val clickedLinkDao: ClickedLinkDao,
    private val linkDao: LinkDao,
) {
    open fun getAllClickedLinks(): Flow<List<ClickedLink>> = clickedLinkDao.getAllClickedLinks()

    open fun getClickedLinksWithDetails(): Flow<List<Pair<ClickedLink, Link?>>> = combine(
        clickedLinkDao.getAllClickedLinks(),
        linkDao.getAllLinks(),
    ) { clickedLinks, allLinks ->
        clickedLinks.map { clickedLink ->
            val link = allLinks.find { it.id == clickedLink.linkId }
            clickedLink to link
        }
    }

    open fun getClickedLinksByLinkId(linkId: String): Flow<List<ClickedLink>> = clickedLinkDao.getClickedLinksByLinkId(linkId)

    open suspend fun insertClickedLink(clickedLink: ClickedLink) = clickedLinkDao.insertClickedLink(clickedLink)

    open suspend fun recordLinkClick(linkId: String) {
        val clickedLink = ClickedLink(linkId = linkId)
        insertClickedLink(clickedLink)
    }

    open suspend fun deleteClickedLinksByLinkId(linkId: String) = clickedLinkDao.deleteClickedLinksByLinkId(linkId)

    open suspend fun deleteAllClickedLinks() = clickedLinkDao.deleteAllClickedLinks()

    open suspend fun getClickCountForLink(linkId: String): Int = clickedLinkDao.getClickCountForLink(linkId)
}
