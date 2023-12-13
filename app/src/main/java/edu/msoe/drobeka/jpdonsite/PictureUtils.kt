/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * PictureUtils.kt
 */

package edu.msoe.drobeka.jpdonsite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

/**
 * This function comes from the provided code in the Big Nerd Ranch Android textbook:
 * https://bignerdranch.com/books/android-programming-the-big-nerd-ranch-guide-5th-edition/
 */
fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap? {
    // read in dimensions of image on the disk
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    // figure out how much to scale down by
    val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) {
        1
    } else {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        minOf(heightScale, widthScale).roundToInt()
    }


    // had to add this in because if it can't decode the file it crashes the application
    // read in and create final bitmap

    val bitmap = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    })
    if(bitmap != null) {
        return bitmap
    }
    return null
}