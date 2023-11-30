package edu.msoe.drobeka.jpdonsite

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class KotlinTesting {
    @Test
    fun toStringListTest() {
        var photos = mutableListOf<String>()
        photos.add("photos1")
        photos.add("photo2")

        var listString = photos.toString()

        val split = listString.replace("[", "").replace("]", "").replace(" ", "").split(",")

        println(split)
        assertEquals(photos, split)
    }
}