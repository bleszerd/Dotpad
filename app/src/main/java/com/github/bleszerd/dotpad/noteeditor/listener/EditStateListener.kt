package com.github.bleszerd.dotpad.noteeditor.listener

import android.content.Context
import android.view.View

/**
Dotpad
09/08/2021 - 09:47
Created by bleszerd.
@author alive2k@programmer.net
 */

/**Implements this listeners allow you to manage note edit state*/
interface EditStateListener {
    fun onInReadMode(context: Context)
    fun onInEditMode(context: Context)
}