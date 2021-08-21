package com.github.bleszerd.dotpad.notehome.contract

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.notehome.listeners.NoteChangeListener
import com.github.bleszerd.dotpad.notehome.view.NoteHomeActivity
import com.google.android.gms.ads.AdRequest

/**
Dotpad
07/08/2021 - 13:02
Created by bleszerd.
@author alive2k@programmer.net
 */
interface NoteHomeContract {
    interface NoteHomeView {
        fun navigateToNoteEditor(intent: Intent)
        fun updateViewNoteList()
        fun configureNoteChangeListener()
        fun showAd(adRequest: AdRequest)
    }

    interface NoteHomePresenter {
        fun getAllNotes()
        fun deleteNote(note: Note)
        fun getUpdatedNoteById(noteId: String): Note?
        fun getLastModifiedNote(): Note?
        fun getNoteList(): MutableList<Note>
        fun openNoteEditor(context: Context, noteData: Note? = null)
        fun getNoteImageWithUri(coverImageUri: String?): Bitmap?
        fun setNoteChangeListener(noteChangeListener: NoteChangeListener)
        fun updateLastEditedNoteDataUi()
        fun verifyFirstLaunch(context: Context)
        fun configureAds(context: Context)
    }
}