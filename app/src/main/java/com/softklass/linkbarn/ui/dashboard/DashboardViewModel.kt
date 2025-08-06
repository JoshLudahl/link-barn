package com.softklass.linkbarn.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.ClickedLink
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.ClickedLinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DashboardViewModel @Inject constructor(
    clickedLinkRepository: ClickedLinkRepository,
) : ViewModel() {

    val clickedLinksWithDetails: StateFlow<List<Pair<ClickedLink, Link?>>> =
        clickedLinkRepository.getClickedLinksWithDetails()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

    val clickedLinks: StateFlow<List<Link>> =
        clickedLinksWithDetails
            .map { clickedLinksWithDetails ->
                clickedLinksWithDetails
                    .mapNotNull { (_, link) -> link }
                    .distinctBy { it.id }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )
}
