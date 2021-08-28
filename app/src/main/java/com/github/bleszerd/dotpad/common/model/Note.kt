package com.github.bleszerd.dotpad.common.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

//Note data model
data class Note(
    var id: String,
    var lasModified: String,
    var text: String,
    var title: String,
    var coverImage: String?,
    var content: List<NoteContent>? = null,
) : Serializable {

    init {
        if (content == null)
            content = mutableListOf()
    }

    fun toSerial(): SerializedNote{
        val gson = Gson()
        val serializedContent = gson.toJson(content)

        return SerializedNote(id, lasModified, text, title, coverImage, serializedContent)
    }
}

data class SerializedNote(
    var id: String,
    var lasModified: String,
    var text: String,
    var title: String,
    var coverImage: String?,
    var content: String?,
) : Serializable {
    init {
        if (content == null)
            content = ""
    }

    fun toNote(): Note{
        val gson = Gson()
        val type = object : TypeToken<MutableList<NoteContent?>?>() {}.type
        val parsedContentData: List<NoteContent>? = gson.fromJson(content, type)

        return Note(id, lasModified, text, title, coverImage, parsedContentData)
    }
}
