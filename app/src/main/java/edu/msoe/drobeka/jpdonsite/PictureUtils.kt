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

    if(BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }) != null) {
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        })
    }

    // read in and create final bitmap
    return null
}