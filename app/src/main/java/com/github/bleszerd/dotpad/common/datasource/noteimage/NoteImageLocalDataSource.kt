package com.github.bleszerd.dotpad.common.datasource.noteimage

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.bleszerd.dotpad.common.constants.Constants.Directories
import java.io.*
import java.nio.Buffer
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

/**
Dotpad
16/08/2021 - 11:12
Created by bleszerd.
@author alive2k@programmer.net
 */
class NoteImageLocalDataSource(context: Context) : NoteImageDataSource {

    //Save image on sdCard
    override fun saveImage(context: Context, imageUri: Uri?): String? {
        if (imageUri == null)
            return null

        //Filename based on current time
        val fileName = "${System.currentTimeMillis()}.jpg"

        var bitmapImage: Bitmap? = null

        try {
            //Generate a inputStream to image
            val imageInputStream = context.contentResolver.openInputStream(imageUri)

            //Convert inputStream to bitmap
            bitmapImage = BitmapFactory.decodeStream(imageInputStream)
        } catch (e: Error) {
            println("ERROR: ${e.message}")
        }

        //Delegate context to another
        val contextWrapper = ContextWrapper(context)

        //Get app image directory
        val directory = contextWrapper.getDir(Directories.SAVED_IMAGES, Context.MODE_PRIVATE)

        //Create image file
        val imageFile = File(directory, fileName)

        //Open outputStream to write file
        val fileOutputStream = FileOutputStream(imageFile)

        try {
            //Compress and write file into outputStream
            bitmapImage?.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
        } catch (e: Error) {
            println("ERROR: ${e.message}")

            return null
        } finally {
            //Close outputStream
            fileOutputStream.close()
        }

        return imageFile.absolutePath
    }

    //Save image on sdCard
    override fun saveImage(context: Context, imageBitmap: Bitmap?): String? {
        if (imageBitmap == null)
            return null

        //Filename based on current time
        val fileName = "${System.currentTimeMillis()}.jpg"

        //Delegate context to another
        val contextWrapper = ContextWrapper(context)

        //Get app image directory
        val directory = contextWrapper.getDir(Directories.SAVED_IMAGES, Context.MODE_PRIVATE)

        //Create image file
        val imageFile = File(directory, fileName)

        //Open outputStream to write file
        val fileOutputStream = FileOutputStream(imageFile)

        try {
            //Compress and write file into outputStream
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
        } catch (e: Error) {
            println("ERROR: ${e.message}")

            return null
        } finally {
            //Close outputStream
            fileOutputStream.close()
        }

        return imageFile.absolutePath
    }

    //Load image from string URI
    override fun loadImage(uri: String?): Bitmap? {
        if (uri == null)
            return null

        return handleLoadImage(File(uri))
    }

    //Load image from file
    override fun loadImage(file: File?): Bitmap? {
        if (file == null)
            return null

        return handleLoadImage(file)
    }

    private fun handleLoadImage(f: File): Bitmap? {
        var imageBitmap: Bitmap? = null

        try {
            val btmpOpts = BitmapFactory.Options()

            //Decode without allocate memory (RAM)
            btmpOpts.inJustDecodeBounds = true

            //Open inputStream to file path
            var fis = FileInputStream(f)

            //Decode inputStream to bitmap
            BitmapFactory.decodeStream(fis, null, btmpOpts)

            //Close inputStream
            fis.close()

            val IMAGE_MAX_SIZE = 500
            var scale = 1

            if (btmpOpts.outHeight > IMAGE_MAX_SIZE || btmpOpts.outWidth > IMAGE_MAX_SIZE) {
                scale =
                    2.0.pow((ln(IMAGE_MAX_SIZE / btmpOpts.outHeight.coerceAtLeast(btmpOpts.outWidth)
                        .toDouble()) / ln(0.5)).roundToInt().toDouble()).toInt()
            }

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()

            //Set sample size for rescale image
            o2.inSampleSize = scale

            //Open inputStream to file path again
            fis = FileInputStream(f)

            //Rescale bitmap to save memory
            imageBitmap = BitmapFactory.decodeStream(fis, null, o2)

            //Close inputStream
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageBitmap
    }
}