package com.github.bleszerd.dotpad.common.datasource.notedata

import com.github.bleszerd.dotpad.common.model.Note

/**
Dotpad
09/08/2021 - 11:40
Created by bleszerd.
@author alive2k@programmer.net
 */

/**Provide a universal database for NoteDataSource*/
interface NoteDataSource {
    //Create
    fun createNote(note: Note): Boolean

    //Read
    fun getAllNotes(): MutableList<Note>
    fun getNoteById(noteId: String): Note?
    fun getLastModifiedNote(): Note?

    //Update
    fun updateNoteById(noteId: String, updatedNote: Note): Boolean

    //Delete
    fun deleteNote(noteId: String): Boolean
}