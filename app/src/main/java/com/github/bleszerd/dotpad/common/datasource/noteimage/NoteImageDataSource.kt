package com.github.bleszerd.dotpad.common.datasource.noteimage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File

/**
Dotpad
16/08/2021 - 11:11
Created by bleszerd.
@author alive2k@programmer.net
 */
interface NoteImageDataSource {
    //Create
    fun saveImage(context: Context, imageUri: Uri?): String?
    fun saveImage(context: Context, imageBitmap: Bitmap?): String?

    //Read
    fun loadImage(uri: String?): Bitmap?
    fun loadImage(file: File?): Bitmap?

    //Update
    // TODO: 17/08/2021
    
    //Delete
    fun deleteImage(fileUri: String?)
}