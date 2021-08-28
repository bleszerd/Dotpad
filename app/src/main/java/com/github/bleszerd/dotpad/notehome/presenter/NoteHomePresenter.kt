package com.github.bleszerd.dotpad.notehome.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.view.WindowManager
import com.github.bleszerd.dotpad.R
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.constants.Constants.ExtrasKeys
import com.github.bleszerd.dotpad.common.datasource.notedata.NoteDataSource
import com.github.bleszerd.dotpad.common.datasource.noteimage.NoteImageDataSource
import com.github.bleszerd.dotpad.common.extensions.isInternalPath
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.common.util.Ads
import com.github.bleszerd.dotpad.noteeditor.view.ActivityNoteEditor
import com.github.bleszerd.dotpad.notehome.contract.NoteHomeContract
import com.github.bleszerd.dotpad.notehome.listeners.NoteChangeListener
import com.google.android.gms.ads.AdView

/**
Dotpad
08/08/2021 - 20:12
Created by bleszerd.
@author alive2k@programmer.net
 */
class NoteHomePresenter(
    private val view: NoteHomeContract.NoteHomeView,
    private val noteDataSource: NoteDataSource,
    private val noteImageSource: NoteImageDataSource,
) :
    NoteHomeContract.NoteHomePresenter {

    private var noteList = mutableListOf<Note>()
    private lateinit var changedListener: NoteChangeListener
    private var noteToUpdateIndex = -1
    private var noteToUpdateId = "-1"

    //Open existent
    override fun openNoteEditor(context: Context, noteData: Note?) {
        val intent = Intent(context, ActivityNoteEditor::class.java)

        // === Note no exists ===
        if (noteData == null) {
            //Navigate to create a new note
            view.navigateToNoteEditor(intent)

            noteToUpdateIndex = noteList.size //Used to update note on UI after database changes

            return
        }

        // === Note exists ===
        val noteIndex = noteList.indexOf(noteData) //Index of note to update

        //Put note data into extra
        intent.putExtra(ExtrasKeys.NOTE_DATA, noteData)

        //Navigate to update note into extras
        view.navigateToNoteEditor(intent)

        noteToUpdateIndex = noteIndex //Used to update note on UI after changes
        noteToUpdateId = noteData.id  //Used to update note on database
    }

    //Get all notes from database
    override fun getAllNotes() {
        noteList = noteDataSource.getAllNotes()
    }

    //Delete note from database
    override fun deleteNote(note: Note) {
        //Get note index
        val noteIndex = noteList.indexOf(note)

        //Verify if note exists into dataset
        if (noteIndex == -1)
            return

        //Delete note from database
        noteDataSource.deleteNote(note.id)

        //Delete note image from sdcard
        noteImageSource.deleteImage(note.coverImage)

        //Remote note from local dataset
        noteList.removeAt(noteIndex)

        //Notify onDeleted listener
        changedListener.onNoteDeletedAt(noteIndex)
    }

    //Update SwapComponent image with URI
    override fun getNoteImageWithUri(coverImageUri: String?): Bitmap? {
        //Verify if is a internal file and not null
        if (coverImageUri.isInternalPath() && coverImageUri != null) {
            //Return image from sdcard
            return noteImageSource.loadImage(coverImageUri)
        }

        return null
    }

    //Return presenter note list to UI
    override fun getNoteList(): MutableList<Note> {
        return noteList
    }

    //Set note change listener to handle CRUD on UI
    override fun setNoteChangeListener(noteChangeListener: NoteChangeListener) {
        this.changedListener = noteChangeListener
    }

    // TODO: 17/08/2021 REPLACE HARDCODED VALUES WITH CONSTANTS
    //Get specific note updated by id to show on UI
    override fun getUpdatedNoteById(noteId: String): Note? {
        //If has valid updateId and updateIndex...
        if (noteToUpdateId != "-1" && noteToUpdateIndex != -1) {

            //Get updated note from database
            val note = noteDataSource.getNoteById(noteId)

            //If note exists update local dataset
            if (note != null)
                noteList[noteToUpdateIndex] = note

            return note
        }

        return null
    }

    // TODO: 17/08/2021 REPLACE HARDCODED VALUES WITH CONSTANTS
    //Get last updated note to show on UI
    override fun getLastModifiedNote(): Note? {
        //If updateId not changed and updateIndex is valid...
        if (noteToUpdateId == "-1" && noteToUpdateIndex != -1) {

            //Get last updated note from database
            val note = noteDataSource.getLastModifiedNote()

            //If note exists add to dataset
            if (note != null)
                noteList.add(note)

            return note
        }

        return null
    }

    // TODO: 18/08/2021 REPLACE HARDCODED VALUES WITH CONSTANTS
    //Update last edited note on UI
    override fun updateLastEditedNoteDataUi() {
        //Note has edited
        if (noteToUpdateId != "-1" && noteToUpdateIndex != -1) {
            val updatedNote = getUpdatedNoteById(noteToUpdateId)
        }

        //Note has created
        if (noteToUpdateId == "-1" && noteToUpdateIndex != -1) {
            val newNote = getLastModifiedNote()
        }

        //Notify update listener of changes
        changedListener.onNoteUpdateAt(noteToUpdateIndex)

        //Restoring default updateId and updateIndex
        resetNoteIndexId()
    }

    //Restore default updateId and updateIndex
    private fun resetNoteIndexId() {
        noteToUpdateIndex = -1
        noteToUpdateId = "-1"
    }

    //If is the first app launch create a tutorial note
    override fun verifyFirstLaunch(context: Context) {

        //Get shared prefs instance
        val sharedPreferences =
            context.getSharedPreferences(Constants.SharedPreferences.FIRST_LAUNCH,
                Context.MODE_PRIVATE)

        //Get first launch value
        val isFirstLaunch =
            sharedPreferences.getBoolean(Constants.SharedPreferences.FIRST_LAUNCH, true)

        //If first launch create a new note to explain this app, else just return
        if (!isFirstLaunch)
            return

        val noteToCreate = Note(
            "1",
            "1",
            context.getString(R.string.tutorial_text),
            context.getString(R.string.tutorial_title),
            "null",
        )
        noteDataSource.createNote(noteToCreate)
        sharedPreferences.edit().putBoolean(Constants.SharedPreferences.FIRST_LAUNCH, false).apply()
    }

    override fun configureAds(wm: WindowManager, adHost: View, context: Context) {
        val adConstructor = Ads(context)
        val ad = adConstructor.buildAd()

        val adView = AdView(context).apply {
            adSize = adConstructor.getAdSize(context, wm, adHost)
            adUnitId = Ads.BANNER_BOTTOM_HOMENOTE_UNIT_ID
        }

        view.showAd(ad, adView)
    }
}