package com.github.bleszerd.dotpad.common.datasource.notedata

import android.content.Context
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.common.model.SerializedNote
import com.github.bleszerd.dotpad.common.util.DatabaseHelper

/**
Dotpad
09/08/2021 - 11:41
Created by bleszerd.
@author alive2k@programmer.net
 */

/**Local database of NoteDataSource implementation*/
class NoteDataLocalDataSource(context: Context) : NoteDataSource {
    private val dbHelper = DatabaseHelper(context)

    //Get all notes from database
    override fun getAllNotes(): MutableList<Note> {
        val db = dbHelper.readableDatabase

        //Start with empty note list
        val noteList = mutableListOf<Note>()

        //Define cursor using SQL raw query
        val cursor = db.rawQuery("SELECT * FROM ${Constants.Database.NOTE_TABLE_NAME}", null)

        //Add all notes into empty noteList
        try {
            if (cursor.moveToFirst()) {
                do {
                    //Fetch note data to create a new one
                    val id = cursor.getString(cursor.getColumnIndex("id"))
                    val lastModified = cursor.getString(cursor.getColumnIndex("last_modified"))
                    val text = cursor.getString(cursor.getColumnIndex("text"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val content = cursor.getString(cursor.getColumnIndex("note_content"))
                    val coverImage = cursor.getString(cursor.getColumnIndex("cover_image"))

                    //Add note into list
                    noteList.add(SerializedNote(id, lastModified, text, title, coverImage, content).toNote())
                } while (cursor.moveToNext())
            }
        } catch (e: Error) {
            e.printStackTrace()
        } finally {
            //Close database connection
            cursor.close()
        }

        return noteList
    }


    //Add a new note into database
    override fun createNote(note: Note): Boolean {
        val db = dbHelper.writableDatabase
        val serializedNote = note.toSerial()

        try {
            //Start database transaction
            db.beginTransaction()

            //Set SQL query
            db.execSQL("INSERT OR IGNORE INTO ${Constants.Database.NOTE_TABLE_NAME} (id, cover_image, title, text, last_modified, note_content) VALUES ('${serializedNote.id}', '${serializedNote.coverImage}', '${serializedNote.title}', '${serializedNote.text}', '${serializedNote.lasModified}', '${serializedNote.content}')")

            //Commit SQL query
            db.setTransactionSuccessful()
        } catch (e: Error) {
            e.printStackTrace()

            return false
        } finally {
            //Close database connection
            db.endTransaction()
        }

        return true
    }

    override fun updateNoteById(noteId: String, updatedNote: Note): Boolean {
        val db = dbHelper.writableDatabase
        val serializedNote = updatedNote.toSerial()

        try {
            //Start database transaction
            db.beginTransaction()

            //Set SQL query
            db.execSQL("UPDATE ${Constants.Database.NOTE_TABLE_NAME} SET title ='${serializedNote.title}', text = '${serializedNote.text}', cover_image = '${serializedNote.coverImage}', last_modified = '${System.currentTimeMillis()}', note_content = '${serializedNote.content}' WHERE id = '$noteId'")

            //Commit SQL query
            db.setTransactionSuccessful()
        } catch (e: Error) {
            e.printStackTrace()

            return false
        } finally {
            //Close database connection
            db.endTransaction()
        }

        return true
    }

    override fun getNoteById(noteId: String): Note? {
        val db = dbHelper.readableDatabase

        //Create a empty variable to store the note
        var note: Note? = null

        //Define cursor using SQL raw query
        val cursor = db.rawQuery("SELECT * FROM ${Constants.Database.NOTE_TABLE_NAME} WHERE id='${noteId}'", null)

        try {
            //Get note data
            if (cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val lastModified = cursor.getString(cursor.getColumnIndex("last_modified"))
                val text = cursor.getString(cursor.getColumnIndex("text"))
                val content = cursor.getString(cursor.getColumnIndex("note_content"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val coverImage = cursor.getString(cursor.getColumnIndex("cover_image"))

                //Update variable with note data
                note = SerializedNote(id, lastModified, text, title, coverImage, content).toNote()
            }
        } catch (e: Error) {
            e.printStackTrace()

            return null
        } finally {
            //Close database connection
            cursor.close()
        }

        return note
    }

    override fun deleteNote(noteId: String): Boolean {
        val db = dbHelper.writableDatabase

        //Set default success response to false
        var success = false

        try {
            //Start database transaction
            db.beginTransaction()

            //Set SQL query
            db.execSQL("DELETE FROM ${Constants.Database.NOTE_TABLE_NAME} WHERE id='${noteId}'")

            //Commit SQL query
            db.setTransactionSuccessful()

            //Set success to true
            success = true
        } catch (e: Error) {
            e.printStackTrace()
        } finally {
            //Close database connection
            db.endTransaction()
        }

        return success
    }

    override fun getLastModifiedNote(): Note? {
        val db = dbHelper.readableDatabase

        //Create a empty variable to store the note
        var note: Note? = null

        //Define cursor using SQL raw query
        val cursor = db.rawQuery("SELECT id, last_modified, text, title, cover_image, note_content, MAX(last_modified) FROM ${Constants.Database.NOTE_TABLE_NAME}",  null)

        //Get note data
        try {
            if (cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val lastModified = cursor.getString(cursor.getColumnIndex("last_modified"))
                val text = cursor.getString(cursor.getColumnIndex("text"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("note_content"))
                val coverImage = cursor.getString(cursor.getColumnIndex("cover_image"))

                //Update variable with note data
                note = SerializedNote(id, lastModified, text, title, coverImage, content).toNote()
            }
        } catch (e: Error) {
            e.printStackTrace()

            return null
        } finally {
            //Close database connection
            cursor.close()
        }

        return note
    }
}