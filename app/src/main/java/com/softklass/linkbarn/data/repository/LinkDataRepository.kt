package com.softklass.linkbarn.data.repository

import com.softklass.linkbarn.data.model.Link
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkDataRepository @Inject constructor() {

    fun getAllLinks(): List<Link> {
        return listOf()
    }
    fun insertLink(link: Link) {}

    fun updateLink(link: Link) {}

    fun deleteLink(id: Long) {}
}
