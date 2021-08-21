package com.github.bleszerd.dotpad.notehome.listeners

/**
Dotpad
17/08/2021 - 10:56
Created by bleszerd.
@author alive2k@programmer.net
 */
interface NoteChangeListener {
    fun onNoteDeletedAt(index: Int)
    fun onNoteUpdateAt(index: Int)
}