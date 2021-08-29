package com.github.bleszerd.dotpad.noteeditor.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.bleszerd.dotpad.R
import com.github.bleszerd.dotpad.common.components.AddViewDialog
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.datasource.notedata.NoteDataLocalDataSource
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.databinding.ActivityNoteEditorBinding
import com.github.bleszerd.dotpad.noteeditor.contract.NoteEditorContract
import com.github.bleszerd.dotpad.common.datasource.noteimage.NoteImageLocalDataSource
import com.github.bleszerd.dotpad.common.model.ContentType
import com.github.bleszerd.dotpad.common.model.NoteContent
import com.github.bleszerd.dotpad.noteeditor.presenter.NoteEditorPresenter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Picasso
import java.util.*

class ActivityNoteEditor : AppCompatActivity(),
    NoteEditorContract.NoteEditorView {

    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var presenter: NoteEditorPresenter
    private lateinit var contentAdapter: NoteContentAdapter
    private var contentData: MutableList<NoteContent> = mutableListOf(NoteContent(ContentType.TYPE_ADD, null))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        presenter = NoteEditorPresenter(this, NoteDataLocalDataSource(this), NoteImageLocalDataSource(this))

        configureToolbar()
        configureFab()
        configureContentRecycler()

        presenter.updateViewInitState(intent.extras)

        setContentView(binding.root)
    }

    private fun configureContentRecycler() {
        presenter.configureRecyclerListeners()

        contentAdapter = NoteContentAdapter()

        binding.includeNoteContentScrolling.noteContentScrollingRecyclerViewContent.apply {
            adapter = contentAdapter
            layoutManager = LinearLayoutManager(this@ActivityNoteEditor)
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.configureAds(windowManager, binding.activityNoteEditorFrameLayoutAdHost, this)
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

        if (noteData.content?.size!! > 0){
            contentData = noteData.content!!.toMutableList()
        }

        //Populate references
//        val scrollingTextInput = binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText
        val headerTitleInput = binding.activityNoteEditorEditTextTitleHeaderInput

        //Set texts
//        scrollingTextInput.setText(noteData.text)
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
//                binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.isEnabled = true
            }
            //Disable all editable fields
            Constants.EditMode.READ_MODE -> {
                binding.activityNoteEditorEditTextTitleHeaderInput.isEnabled = false
                binding.activityNoteEditorFabHandleEditMode.requestFocus()
//                binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.isEnabled = false
            }
        }
    }

    //Generate a new note from inputs
    override fun generateNoteFromInputs(): Note {
        val title = binding.activityNoteEditorEditTextTitleHeaderInput.text.toString()
//        val text = binding.includeNoteContentScrolling.activityNoteEditorEditTextScrollingText.text.toString()
        val coverImage = null
        val lastModified = System.currentTimeMillis().toString()
        val id = "${title}_${lastModified}"

        return Note(id, lastModified, "", title, coverImage, contentData)
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

    override fun showAd(adRequest: AdRequest, adView: AdView) {
        binding.activityNoteEditorFrameLayoutAdHost.addView(adView)
        adView.loadAd(adRequest)
    }

    override fun addBlockToContent(noteContent: NoteContent) {
        contentData.add(noteContent)
        Collections.swap(contentData, contentData.size - 2, contentData.size -1)
        contentAdapter.notifyItemMoved(contentData.size - 2, contentData.size -1)
        contentAdapter.notifyItemInserted(contentData.size)
    }

    override fun getContentData(): MutableList<NoteContent> {
        return contentData
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

    inner class NoteContentAdapter: RecyclerView.Adapter<BaseNoteContentViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): BaseNoteContentViewHolder {
            return when(viewType){
                ContentType.TYPE_TEXT.value -> ContentTextViewHolder(LayoutInflater.from(this@ActivityNoteEditor).inflate(R.layout.content_text, parent, false))
                ContentType.TYPE_IMAGE.value -> ContentImageViewHolder(LayoutInflater.from(this@ActivityNoteEditor).inflate(R.layout.content_image, parent, false))
                else -> ContentAddViewHolder(LayoutInflater.from(this@ActivityNoteEditor).inflate(R.layout.content_add, parent, false))
            }
        }

        override fun getItemViewType(position: Int): Int {
            return contentData[position].contentType.value
        }

        override fun onBindViewHolder(holder: BaseNoteContentViewHolder, position: Int) {
            holder.bind(contentData[position])
        }

        override fun getItemCount(): Int {
            return contentData.size
        }
    }

    abstract class BaseNoteContentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(data: NoteContent)
    }

    inner class ContentTextViewHolder(itemView: View) : BaseNoteContentViewHolder(itemView){
        override fun bind(data: NoteContent) { itemView as TextView
            itemView.apply {
                text = data.data
                id = data.contentId!!

                setOnFocusChangeListener { view, focused ->
                    presenter.handleInputFocus(view, focused)
                }
            }
        }
    }

    inner class ContentImageViewHolder(itemView: View) : BaseNoteContentViewHolder(itemView){
        override fun bind(data: NoteContent) { itemView as ImageView
            Picasso.get()
                .load(data.data)
                .into(itemView)
        }
    }

    inner class ContentAddViewHolder(itemView: View) : BaseNoteContentViewHolder(itemView){
        override fun bind(data: NoteContent) {
            itemView.setOnClickListener {
                AddViewDialog(this@ActivityNoteEditor)
                    .addListener(presenter.getAddViewDialogListener())
                    .show()
            }
        }
    }
}