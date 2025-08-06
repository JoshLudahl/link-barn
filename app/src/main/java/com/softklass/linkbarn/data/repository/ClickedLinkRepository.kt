package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.db.dao.ClickedLinkDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.ClickedLink
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

open class ClickedLinkRepository @Inject constructor(
    private val clickedLinkDao: ClickedLinkDao,
    private val linkDao: LinkDao,
) {
    open suspend fun insertClickedLink(clickedLink: ClickedLink) = clickedLinkDao.insertClickedLink(clickedLink)

    open suspend fun recordLinkClick(linkId: String) {
        val clickedLink = ClickedLink(linkId = linkId)
        insertClickedLink(clickedLink)
    }

    open fun getLinksOrderedByClickCount(): Flow<List<Link>> = combine(
        clickedLinkDao.getLinksOrderedByClickCount(),
        linkDao.getAllLinks(),
    ) { linkClickCounts, allLinks ->
        linkClickCounts.mapNotNull { linkClickCount ->
            allLinks.find { it.id == linkClickCount.linkId }
        }
    }
}
