package edu.msoe.drobeka.jpdonsite.jobs

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

data class Job(
    val id: String,
    val title: String,
    val description: String,
    val photos: MutableList<String> = arrayListOf()
)