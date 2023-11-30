package edu.msoe.drobeka.jpdonsite.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.msoe.drobeka.jpdonsite.jobs.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Job::class], version=1)
@TypeConverters(JobConverters::class)
abstract class JobDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao

    fun clearTables() {
        GlobalScope.launch(Dispatchers.IO) {
            this@JobDatabase.clearAllTables()
        }
    }
}