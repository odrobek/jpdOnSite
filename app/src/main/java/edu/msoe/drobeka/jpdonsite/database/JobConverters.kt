package edu.msoe.drobeka.jpdonsite.database

import androidx.room.TypeConverter

class JobConverters {

    @TypeConverter
    fun fromPhotos(photos: MutableList<String>): String {
        return photos.toString()
    }

    @TypeConverter
    fun toPhotos(photos: String): MutableList<String> {
        return photos.replace("[", "").replace("]", "").replace(" ", "").split(",").toMutableList()
    }
}