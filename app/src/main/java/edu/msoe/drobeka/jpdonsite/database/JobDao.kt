package edu.msoe.drobeka.jpdonsite.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface JobDao {
    @Query("SELECT * FROM job")
    fun getJobs(): Flow<List<Job>>

    @Query("SELECT * FROM job WHERE id=(:id)")
    fun getJob(id: UUID): Job

    @Update
    fun updateJob(job: Job)

    @Insert
    fun addJob(job: Job)

}