package com.github.bleszerd.dotpad.noteeditor.contract

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.model.Note
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

/**
Dotpad
08/08/2021 - 20:41
Created by bleszerd.
@author alive2k@programmer.net
 */
interface NoteEditorContract {
    interface NoteEditorView {
        fun updateNoteDataOnUi(noteData: Note)
        fun toggleUiEditMode(editDrawable: Drawable, editMode: Constants.EditMode)
        fun generateNoteFromInputs(): Note
        fun getContext(): Context
        fun launchGalleryAndReturnUri(intent: Intent, requestCode: Int)
        fun updateToolbarHeaderImage(imageUri: Uri?)
        fun launchCameraAndReturnUri(intent: Intent, requestCode: Int)
        fun updateToolbarHeaderImage(bitmap: Bitmap?)
        fun showAd(adRequest: AdRequest, adView: AdView)
    }

    interface NoteEditorPresenter {
        fun updateViewInitState(extras: Bundle?)
        fun saveNote(note: Note)
        fun changeEditMode()
        fun openImageLoader()
        fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun updateHeaderImage(coverImage: String?)
        fun getNoteDataFromIntent(extras: Bundle?)
        fun configureAds(wm: WindowManager, adHost: View, context: Context)
    }
}