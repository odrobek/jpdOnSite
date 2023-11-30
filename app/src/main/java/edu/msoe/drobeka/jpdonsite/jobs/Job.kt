package edu.msoe.drobeka.jpdonsite.jobs

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Job(
    @PrimaryKey val id: UUID,
    val title: String,
    val description: String,
    val photos: MutableList<String> = arrayListOf()
)