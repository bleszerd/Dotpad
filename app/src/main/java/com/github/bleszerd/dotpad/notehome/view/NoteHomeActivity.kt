package com.github.bleszerd.dotpad.notehome.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.bleszerd.dotpad.R
import com.github.bleszerd.dotpad.common.components.NoteSwapView
import com.github.bleszerd.dotpad.common.datasource.notedata.NoteDataLocalDataSource
import com.github.bleszerd.dotpad.common.datasource.noteimage.NoteImageLocalDataSource
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.databinding.ActivityNoteHomeBinding
import com.github.bleszerd.dotpad.notehome.contract.NoteHomeContract
import com.github.bleszerd.dotpad.notehome.listeners.NoteChangeListener
import com.github.bleszerd.dotpad.notehome.presenter.NoteHomePresenter
import com.google.android.gms.ads.AdRequest

class NoteHomeActivity : AppCompatActivity(), NoteHomeContract.NoteHomeView {

    private lateinit var binding: ActivityNoteHomeBinding
    private lateinit var presenter: NoteHomeContract.NoteHomePresenter

    //Clone of note list into presenter
    private lateinit var noteList: MutableList<Note>

    private var noteAdapter = NoteAdapter()

    //Note swap callback
    private val noteSwapListener = object : NoteSwapView.NoteSwapListener {
        override fun onItemSelect(noteView: NoteSwapView, noteData: Note) {
            presenter.openNoteEditor(this@NoteHomeActivity, noteData)
        }

        override fun onItemDelete(noteData: Note) {
            presenter.deleteNote(noteData)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteHomeBinding.inflate(layoutInflater)
        presenter =
            NoteHomePresenter(this, NoteDataLocalDataSource(this), NoteImageLocalDataSource(this))

        presenter.verifyFirstLaunch(this)
        configureNoteChangeListener()
        configureRecyclerNoteList()
        configureAddNoteButton()
        setSupportActionBar(binding.includeNoteHomeToolbar.activityNoteHomeToolbarHeaderToolbar)

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        presenter.configureAds(this)

        //Update on UI the last edited note
        presenter.updateLastEditedNoteDataUi()
    }

    //Set add note button event
    private fun configureAddNoteButton() {
        binding.includeNoteHomeToolbar.activityNoteHomeButtonAddNoteButton.setOnClickListener {
            presenter.openNoteEditor(this)
        }
    }

    //Handle note CRUD on UI
    override fun configureNoteChangeListener() {
        presenter.setNoteChangeListener(object : NoteChangeListener {
            override fun onNoteDeletedAt(index: Int) {
                updateViewNoteList()
                noteAdapter.notifyItemRemoved(index)
            }

            override fun onNoteUpdateAt(index: Int) {
                updateViewNoteList()
                noteAdapter.notifyItemChanged(index)
            }
        })
    }

    override fun showAd(adRequest: AdRequest) {
        binding.activityNoteHomeAdViewAd.loadAd(adRequest)
    }

    private fun configureRecyclerNoteList() {
        //Get all notes from database
        presenter.getAllNotes()

        binding.activityNoteHomeRecyclerViewNoteList
        binding.activityNoteHomeRecyclerViewNoteList.adapter = noteAdapter
        binding.activityNoteHomeRecyclerViewNoteList.layoutManager = LinearLayoutManager(this)
    }

    //Open note editor activity
    override fun navigateToNoteEditor(intent: Intent) {
        startActivity(intent)
    }

    //Copy note list from presenter
    override fun updateViewNoteList() {
        this.noteList = presenter.getNoteList()
    }

    //Custom adapter for SwapComponent recycler view
    inner class NoteAdapter : RecyclerView.Adapter<NoteViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): NoteViewHolder {
            return NoteViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.note_home_item, parent, false) as NoteSwapView)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            holder.bind(noteList[position])
        }

        override fun getItemCount(): Int {
            return noteList.size
        }
    }

    //Custom view holder for SwapComponent adapter
    inner class NoteViewHolder(private val noteView: NoteSwapView) :
        RecyclerView.ViewHolder(noteView) {
        fun bind(note: Note) {
            //Set SwapComponent actions listener
            noteView.listener = noteSwapListener
            //Set SwapComponent note data
            noteView.noteData = note

            //Set SwapComponent UI title
            noteView.title = note.title
            //Set SwapComponent UI text
            noteView.text = note.text

            println(presenter.getNoteImageWithUri(note.coverImage))

            //Update SwapComponentImage
            noteView.setCoverImageBitmap(presenter.getNoteImageWithUri(note.coverImage))
        }
    }
}