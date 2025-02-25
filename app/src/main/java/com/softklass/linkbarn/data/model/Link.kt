package com.softklass.linkbarn.data.model

import java.net.URI
import java.time.Instant
import java.util.UUID

data class Link(
    val categories: List<Category?> = listOf(),
    val id: String = UUID.randomUUID().toString(),
    val labels: List<Label?> = listOf(),
    val name: String? = null,
    val namespace: String = "Link",
    val status: Status = Status.UNREAD,
    val updated: Instant = Instant.now(),
    val uri: URI,
    val visited: Boolean = false,
)
