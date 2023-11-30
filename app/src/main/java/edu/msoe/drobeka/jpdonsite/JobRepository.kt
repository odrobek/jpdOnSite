package edu.msoe.drobeka.jpdonsite

import android.content.Context
import androidx.room.Room
import edu.msoe.drobeka.jpdonsite.database.JobDatabase
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import java.util.UUID

private const val DATABASE_NAME = "job-database"

class JobRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    private val database: JobDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            JobDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    fun getJobs(): Flow<List<Job>> = database.jobDao().getJobs()

    fun getJob(id: UUID): Job = database.jobDao().getJob(id)
    fun addJob(job: Job) = database.jobDao().addJob(job)

    fun clearDB() = database.clearTables()

    companion object {
        private var INSTANCE: JobRepository? =  null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = JobRepository(context)
            }
        }

        fun get(): JobRepository {
            return INSTANCE ?:
            throw IllegalStateException("JobRepository must be initialized")
        }
    }
}