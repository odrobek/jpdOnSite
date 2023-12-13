/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * JobListViewModel.kt
 */

package edu.msoe.drobeka.jpdonsite.joblist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobListViewModel : ViewModel() {

    private val _jobs: MutableStateFlow<List<Job>> = MutableStateFlow(emptyList())
    val jobs: StateFlow<List<Job>>
        get() = _jobs.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }

    fun clearDB() {

    }
}