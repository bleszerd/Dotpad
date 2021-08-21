package com.github.bleszerd.dotpad.common.constants

/**
Dotpad
09/08/2021 - 09:49
Created by bleszerd.
@author alive2k@programmer.net
 */

/** Contains Enums and Constants of application like extras, database and EditModes keys*/
object Constants {

    //Used to manage note edit mode
    enum class EditMode {
        EDIT_MODE,
        READ_MODE,
    }

    //Used to find and set extra values
    object ExtrasKeys {
        const val NOTE_DATA = "note_data"
    }

    //Used to manage and access database
    object Database {
        const val DATABASE_NAME = "note_database"
        const val DATABASE_VERSION = 1
        const val NOTE_TABLE_NAME = "notes"
    }

    //Used to handle file types
    object MimeType {
        const val ALL_IMAGES = "image/*"
    }

    //Used to handle file paths
    object Directories {
        const val SAVED_IMAGES = "saved_images"
    }

    //User to handle shared prefs.
    object SharedPreferences {
        private const val SHARED_PREFS_PREFIX = "com.github.bleszerd.dotpad@sharedprefs"

        const val FIRST_LAUNCH = "${SHARED_PREFS_PREFIX}_firstLaunch"
    }
}