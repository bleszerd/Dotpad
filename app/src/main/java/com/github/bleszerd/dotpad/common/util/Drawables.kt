package com.github.bleszerd.dotpad.common.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
Dotpad
09/08/2021 - 10:01
Created by bleszerd.
@author alive2k@programmer.net
 */

//Used for search existing drawables into resource folder
object Drawables {
    fun findDrawable(context: Context, @DrawableRes drawableId: Int): Drawable {
        return ContextCompat.getDrawable(context, drawableId)!!
    }
}