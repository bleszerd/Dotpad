package com.github.bleszerd.dotpad.noteeditor.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.datasource.notedata.NoteDataLocalDataSource
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.databinding.ActivityNoteEditorBinding
import com.github.bleszerd.dotpad.noteeditor.contract.NoteEditorContract
import com.github.bleszerd.dotpad.common.datasource.noteimage.NoteImageLocalDataSource
import com.github.bleszerd.dotpad.noteeditor.presenter.NoteEditorPresenter
import com.google.android.gms.ads.AdRequest

class ActivityNoteEditor : AppCompatActivity(),
    NoteEditorContract.NoteEditorView {

    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var presenter: NoteEditorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        presenter = NoteEditorPresenter(this, NoteDataLocalDataSource(this), NoteImageLocalDataSource(this))
        
        configureToolbar()
        configureFab()

        presenter.updateViewInitState(intent.extras)

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        presenter.configureAds(this)
    }

    //Set fab listeners
    private fun configureFab() {
        binding.activityNoteEditorFabHandleEditMode.setOnClickListener { view ->

            //Toggle edit mode
            presenter.changeEditMode()
        }
    }

    //Set toolbar and listeners
    private fun configureToolbar() {
        //Set toolbar
        val toolbar = binding.activityNoteEditorToolbarHeaderToolbar
        setSupportActionBar(toolbar)

        //Set header image click events
        binding.activityNoteEditorImageViewHeaderImage.setOnClickListener {

            //Open image loader to select a new image
            presenter.openImageLoader()
        }
    }

    //Update UI elements with note from data
    override fun updateNoteDataOnUi(noteData: Note) {

        //Set toolbar title
        supportActionBar?.title = noteData.title

        //Populate references
        val scrollingTextInput = binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText
        val headerTitleInput = binding.activityNoteEditorEditTextTitleHeaderInput

        //Set texts
        scrollingTextInput.setText(noteData.text)
        headerTitleInput.setText(noteData.title)

        //Set header image
        presenter.updateHeaderImage(noteData.coverImage)
    }

    //Update UI edit mode
    override fun toggleUiEditMode(editDrawable: Drawable, editMode: Constants.EditMode) {

        //Populate references
        val fab = binding.activityNoteEditorFabHandleEditMode

        //Update fab drawable
        fab.setImageDrawable(editDrawable)

        when (editMode) {
            //Enable all editable fields
            Constants.EditMode.EDIT_MODE -> {
                binding.activityNoteEditorEditTextTitleHeaderInput.isEnabled = true
                binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.isEnabled = true
            }
            //Disable all editable fields
            Constants.EditMode.READ_MODE -> {
                binding.activityNoteEditorEditTextTitleHeaderInput.isEnabled = false
                binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.isEnabled = false
            }
        }
    }

    //Generate a new note from inputs
    override fun generateNoteFromInputs(): Note {
        val title = binding.activityNoteEditorEditTextTitleHeaderInput.text.toString()
        val text = binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.text.toString()
        val coverImage = null
        val lastModified = System.currentTimeMillis().toString()
        val id = "${title}_${lastModified}"

        return Note(id, lastModified, text, title, coverImage)
    }

    //Return current context of activity
    override fun getContext(): Context {
        return this
    }

    //Launch gallery to select an image
    override fun launchGalleryAndReturnUri(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    //Launch gallery to take photo
    override fun launchCameraAndReturnUri(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun showAd(adRequest: AdRequest) {
        binding.activityNoteEditorAdViewAd.loadAd(adRequest)
    }

    //Update toolbar image on UI
    override fun updateToolbarHeaderImage(imageUri: Uri?) {
        binding.activityNoteEditorImageViewHeaderImage.setImageURI(imageUri)
    }

    override fun updateToolbarHeaderImage(bitmap: Bitmap?) {
        binding.activityNoteEditorImageViewHeaderImage.setImageBitmap(bitmap)
    }

    //Handle photo selector result codes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Delegate response action to presenter
        presenter.handleActivityResult(requestCode, resultCode, data)
    }
}