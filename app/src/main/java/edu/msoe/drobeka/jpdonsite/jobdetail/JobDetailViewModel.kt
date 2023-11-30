package edu.msoe.drobeka.jpdonsite.jobdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.msoe.drobeka.jpdonsite.JobRepository
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class JobDetailViewModel(jobId: UUID) : ViewModel() {
    private val jobRepository = JobRepository.get()

    private val _job: MutableStateFlow<Job?> = MutableStateFlow(null)
    val job: StateFlow<Job?> = _job.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _job.value = jobRepository.getJob(jobId)
        }
    }

    fun updateJob(onUpdate: (Job) -> Job) {
        _job.update { oldJob ->
            oldJob?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()

        job.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                jobRepository.updateJob(it)
            }
        }
    }
}

class JobDetailViewModelFactory(
    private val jobId: UUID
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return JobDetailViewModel(jobId) as T
    }
}