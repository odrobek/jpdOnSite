/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * JobDetailViewModel.kt
 */
package edu.msoe.drobeka.jpdonsite.jobdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.msoe.drobeka.jpdonsite.googledrive.GoogleDrive
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class JobDetailViewModel(folderId: String) : ViewModel() {

    private val _job: MutableStateFlow<Job?> = MutableStateFlow(null)
    val job: StateFlow<Job?> = _job.asStateFlow()

    init {
//        job.collect {
//            GoogleDrive.get().drive.Files()
//        }
    }

    fun updateJob(onUpdate: (Job) -> Job) {
        _job.update { oldJob ->
            oldJob?.let { onUpdate(it) }
        }
    }

    fun afterPhotoChange() {
        job.value?.let {

        }
    }

}

class JobDetailViewModelFactory(
    private val folderId: String
) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return JobDetailViewModel(folderId) as T
    }
}