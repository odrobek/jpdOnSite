package edu.msoe.drobeka.jpdonsite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobListViewModel : ViewModel() {

    private val jobRepository = JobRepository.get()

    private val _jobs: MutableStateFlow<List<Job>> = MutableStateFlow(emptyList())
    val jobs: StateFlow<List<Job>>
        get() = _jobs.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.getJobs().collect {
                _jobs.value = it
            }
        }
    }

    fun addJob(job: Job) {
        jobRepository.addJob(job)
    }

    fun clearDB() {
        jobRepository.clearDB()
    }
}