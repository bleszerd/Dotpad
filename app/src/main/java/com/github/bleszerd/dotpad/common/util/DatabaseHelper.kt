package com.github.bleszerd.dotpad.common.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.bleszerd.dotpad.common.constants.Constants

/**
Dotpad
09/08/2021 - 11:43
Created by bleszerd.
@author alive2k@programmer.net
 */

//Create a connection, tables and a easy way to connect into database
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, Constants.Database.DATABASE_NAME, null, Constants.Database.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS ${Constants.Database.NOTE_TABLE_NAME}(id VARCHAR PRIMARY KEY, cover_image TEXT, title TEXT, text VARCHAR, last_modified TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("onUpgrade Called")
    }
}