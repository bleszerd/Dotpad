package com.github.bleszerd.dotpad.noteeditor.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.github.bleszerd.dotpad.R
import com.github.bleszerd.dotpad.common.components.AddViewDialog
import com.github.bleszerd.dotpad.common.components.ImageLoaderDialog
import com.github.bleszerd.dotpad.common.constants.Constants.EditMode
import com.github.bleszerd.dotpad.common.constants.Constants.ExtrasKeys
import com.github.bleszerd.dotpad.common.datasource.notedata.NoteDataSource
import com.github.bleszerd.dotpad.common.datasource.noteimage.NoteImageDataSource
import com.github.bleszerd.dotpad.common.extensions.findDrawable
import com.github.bleszerd.dotpad.common.extensions.isInternalPath
import com.github.bleszerd.dotpad.common.model.ContentType
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.common.model.NoteContent
import com.github.bleszerd.dotpad.common.util.Ads
import com.github.bleszerd.dotpad.noteeditor.contract.NoteEditorContract
import com.github.bleszerd.dotpad.noteeditor.listener.EditStateListener
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

/**
Dotpad
08/08/2021 - 20:41
Created by bleszerd.
@author alive2k@programmer.net
 */
class NoteEditorPresenter(
    private val view: NoteEditorContract.NoteEditorView,
    private val noteDataSource: NoteDataSource,
    private val noteImageDataSource: NoteImageDataSource,
) :
    NoteEditorContract.NoteEditorPresenter, EditStateListener {

    private var imageDialog: ImageLoaderDialog? = null
    private lateinit var noteData: Note
    private var editModeState = EditMode.READ_MODE
    private var isAnewNote = false
    private var lastSelectedInputId: Int? = -1

    //Handle image selector dialog events
    private val headerImageLoaderListener = object : ImageLoaderDialog.ImageDialogListener {
        override fun onConfirmWebUrl(url: String) {
            handleSelectedPhotoFromWeb(url)
        }

        override fun onOpenGalleryAction(intent: Intent, requestCode: Int) {
            view.launchGalleryAndReturnUri(intent, requestCode)
        }

        override fun onOpenCameraAction(intent: Intent, requestCode: Int) {
            view.launchCameraAndReturnUri(intent, requestCode)
        }
    }

    private val contentImageLoaderListener = object : ImageLoaderDialog.ImageDialogListener {
        override fun onConfirmWebUrl(url: String) {
            val noteContent = NoteContent(ContentType.TYPE_IMAGE, url)
            view.addBlockToContent(noteContent)
        }

        override fun onOpenGalleryAction(intent: Intent, requestCode: Int) {
            // TODO: 28/08/2021  
        }

        override fun onOpenCameraAction(intent: Intent, requestCode: Int) {
            // TODO: 28/08/2021
        }
    }

    private val addViewDialogListener = object : AddViewDialog.AddViewDialogListener {
        override fun onTextSelected() {
            view.addBlockToContent(NoteContent(ContentType.TYPE_TEXT, "Hello world!"))
        }

        override fun onImageSelected(context: Context) {
            ImageLoaderDialog(context)
                .addListener(contentImageLoaderListener)
                .show()
        }

    }

    //Handle view start
    override fun updateViewInitState(extras: Bundle?) {
        getNoteDataFromIntent(extras)
        changeEditMode()
    }

    //Update editor note data and update UI
    override fun getNoteDataFromIntent(extras: Bundle?) {

        if (extras != null && extras.containsKey(ExtrasKeys.NOTE_DATA)) {

            //Update local data
            this.noteData = extras.getSerializable(ExtrasKeys.NOTE_DATA) as Note

            //Update UI
            view.updateNoteDataOnUi(noteData)

            return
        }

        //If there is no data, the note is new.
        isAnewNote = true
    }

    override fun configureAds(wm: WindowManager, adHost: View, context: Context) {
        val adConstructor = Ads(context)
        val ad = adConstructor.buildAd()

        val adView = AdView(context).apply {
            adSize = adConstructor.getAdSize(context, wm, adHost)
            adUnitId = Ads.BANNER_BOTTOM_EDITNOTE_UNIT_ID
        }

        view.showAd(ad, adView)
    }

    override fun configureRecyclerListeners() {

    }

    override fun getAddViewDialogListener(): AddViewDialog.AddViewDialogListener {
        return addViewDialogListener
    }

    override fun handleInputFocus(view: View?, focused: Boolean) {
        view as TextView?

        if (focused) {
            lastSelectedInputId = view?.id
            view?.addTextChangedListener {
                this.view.getContentData().map {
                    if (it.contentId == lastSelectedInputId) {
                        it.data = view.text.toString()
                        return@map
                    }
                }
            }
        }
    }

    //Add a new note into database
    override fun saveNote(note: Note) {
        noteDataSource.createNote(note)
    }

    /**
     * Toggle edit mode.
     * Edit mode is used to enable or disable editable fields like inputs or image selector
     * EDIT_MODE -> User can edit note (inputs and images)
     * READ_MODE -> User only can read the note
     */
    override fun changeEditMode() {
        editModeState = when (editModeState) {
            EditMode.READ_MODE -> {
                onInReadMode(view.getContext())
                EditMode.EDIT_MODE
            }
            EditMode.EDIT_MODE -> {
                onInEditMode(view.getContext())
                EditMode.READ_MODE
            }
        }
    }

    /**
     * Called when the state is changed to Read
     * Notes are saved when the user switches to this mode.
     */
    override fun onInReadMode(context: Context) {

        //If note no exists create a new one
        if (isAnewNote) {
            val noteFromInput = view.generateNoteFromInputs()

            //Update local noteData with input data
            this.noteData = noteFromInput

            //Save note into database
            noteDataSource.createNote(noteFromInput)

            //Note now exists
            isAnewNote = false
        }

        //If note exists update based on user inputs
        else {
            val noteFromInput = view.generateNoteFromInputs()

            //Set the same attributes for a non editable fields
            noteFromInput.id = noteData.id
            noteFromInput.coverImage = noteData.coverImage

            //Update only if user changes something in inputs
            noteDataSource.updateNoteById(noteData.id, noteFromInput)
        }

        //Get drawable to update EDIT_MODE state
        val editDrawable = context.findDrawable(R.drawable.ic_edit)

        //Change edit mode on UI
        view.toggleUiEditMode(editDrawable, editModeState)
    }

    //Called when the state is changed to Edit
    override fun onInEditMode(context: Context) {
        //Get drawable to update READ_MODE state
        val saveDrawable = context.findDrawable(R.drawable.ic_save)

        //Change edit mode on UI
        view.toggleUiEditMode(saveDrawable, editModeState)
    }

    //Open image loader dialog
    override fun openImageLoader() {
        // Can only be opened in edit mode.
        if (editModeState == EditMode.READ_MODE) {
            //Update presenter image dialog
            this.imageDialog = ImageLoaderDialog(view.getContext(), R.style.ImageLoaderDialog)

            //Show dialog and set listeners
            imageDialog?.apply {
                addListener(headerImageLoaderListener)
                show()
            }
        }
    }

    //Handle image loader gallery and camera results
    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            //Handle gallery image picker result
            ImageLoaderDialog.SELECT_PICTURE_GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    handleSelectedGalleryImage(data)
                }
            }
            //Handle camera result
            ImageLoaderDialog.TAKE_PICTURE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    handleCameraPicture(data)
                }
            }
        }
    }

    //Update header toolbar image with URL on UI
    override fun updateHeaderImage(coverImage: String?) {

        //Verify if string path is internal
        if (coverImage.isInternalPath())
            view.updateToolbarHeaderImage(Uri.parse(coverImage))
    }

    //Handle selected gallery image
    private fun handleSelectedGalleryImage(data: Intent?) {

        //Get URI from selected image
        val imageUri = data?.data

        //Update header image on UI
        view.updateToolbarHeaderImage(imageUri)

        //Save image and return the URL of new file
        val internalImageUri = noteImageDataSource.saveImage(view.getContext(), imageUri)

        //Save cover image on current note data
        noteData.coverImage = internalImageUri

        //Update note based on current note data
        noteDataSource.updateNoteById(noteData.id, noteData)
    }

    //Handle selected camera image
    private fun handleCameraPicture(data: Intent?) {
        //Get bitmap from captured image
        var imageBitmap = imageDialog?.grabImage(view.getContext())

        if (imageBitmap != null) {
            //Fix image rotation
            val matrix = Matrix()
            matrix.postRotate(90f)

            //Create rotated image
            imageBitmap = Bitmap.createBitmap(
                imageBitmap,
                0,
                0,
                imageBitmap.width,
                imageBitmap.height,
                matrix,
                true
            )
        }

        //Update header image on UI
        view.updateToolbarHeaderImage(imageBitmap)

        //Save image and return the URL of new file
        val internalImageUri = noteImageDataSource.saveImage(view.getContext(), imageBitmap)

        //Save cover image on current note data
        noteData.coverImage = internalImageUri

        //Update note based on current note data
        noteDataSource.updateNoteById(noteData.id, noteData)
    }

    private fun handleSelectedPhotoFromWeb(url: String) {
        val picassoTarget = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                view.updateToolbarHeaderImage(bitmap)

                //Save image and return the URL of new file
                val internalImageUri = noteImageDataSource.saveImage(view.getContext(), bitmap)

                //Save cover image on current note data
                noteData.coverImage = internalImageUri

                //Update note based on current note data
                noteDataSource.updateNoteById(noteData.id, noteData)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                e?.printStackTrace()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        }

        Picasso
            .get()
            .load(url)
            .into(picassoTarget)
    }
}