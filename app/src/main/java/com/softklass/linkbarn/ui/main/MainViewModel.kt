package com.softklass.linkbarn.ui.main

import androidx.lifecycle.ViewModel
import com.softklass.linkbarn.data.repository.LinkDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val linkDataRepository: LinkDataRepository
) : ViewModel() {
    fun getAllLinks() = linkDataRepository.getAllLinks()
}