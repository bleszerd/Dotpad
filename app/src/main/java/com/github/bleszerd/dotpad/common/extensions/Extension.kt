package com.github.bleszerd.dotpad.common.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.github.bleszerd.dotpad.common.util.Drawables
import java.io.File

/**
Dotpad
07/08/2021 - 12:57
Created by bleszerd.
@author alive2k@programmer.net
 */

//Find drawable by context
fun Context.findDrawable(@DrawableRes drawableId: Int): Drawable {
    return Drawables.findDrawable(this, drawableId)
}

//Return true if url string is an internal system path
fun String?.isInternalPath(): Boolean {
    if (this == null)
        return false

    val separator = File.separator
    return this.startsWith("${separator}data")
}

fun Float.toDp(context: Context): Float {
    return this * context.resources.displayMetrics.density
}