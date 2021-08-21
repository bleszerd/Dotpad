package com.github.bleszerd.dotpad.common.model

import java.io.Serializable

//Note data model
data class Note(
    var id: String,
    var lasModified: String,
    var text: String,
    var title: String,
    var coverImage: String?
) : Serializable {

    //Return true if text and title are the same
    fun inputIsEquals(note: Note): Boolean {
        return text == note.text && title == note.title
    }
}