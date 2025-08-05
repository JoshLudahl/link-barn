package com.softklass.linkbarn.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.ClickedLink
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.ClickedLinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val clickedLinkRepository: ClickedLinkRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    // Get clicked links with their full details
    val clickedLinksWithDetails: StateFlow<List<Pair<ClickedLink, Link?>>> =
        clickedLinkRepository.getClickedLinksWithDetails()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    // Get only the links that have been clicked (filtering out null links)
    val clickedLinks: StateFlow<List<Link>> =
        clickedLinksWithDetails
            .map { clickedLinksWithDetails ->
                clickedLinksWithDetails
                    .mapNotNull { (_, link) -> link }
                    .distinctBy { it.id } // Remove duplicates if same link was clicked multiple times
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    // Get the count of total clicks
    val totalClicksCount: StateFlow<Int> =
        clickedLinksWithDetails
            .map { it.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0,
            )

    // Get the count of unique links clicked
    val uniqueLinksClickedCount: StateFlow<Int> =
        clickedLinks
            .map { it.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0,
            )
}
