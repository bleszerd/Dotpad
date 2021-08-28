package com.github.bleszerd.dotpad.common.model

import java.io.Serializable
import java.util.*

enum class ContentType (val value: Int){
    TYPE_TEXT(1),
    TYPE_IMAGE(2),
    TYPE_ADD(3),
}

data class NoteContent(
    var contentType: ContentType,
    var data: String?,
    var contentId: Int? = null
) : Serializable {
    init {
        if (contentId == null)
            contentId = (Random().nextInt() + Random().nextInt()) / 2
    }
}
