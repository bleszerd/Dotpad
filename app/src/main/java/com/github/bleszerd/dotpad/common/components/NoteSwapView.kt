package com.github.bleszerd.dotpad.common.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.bleszerd.dotpad.R
import com.github.bleszerd.dotpad.common.extensions.findDrawable
import com.github.bleszerd.dotpad.common.extensions.toDp
import com.github.bleszerd.dotpad.common.model.Note
import com.github.bleszerd.dotpad.databinding.NoteSwapViewBinding

/**
Dotpad
10/08/2021 - 13:59
Created by bleszerd.
@author alive2k@programmer.net
 */
class NoteSwapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = NoteSwapViewBinding.inflate(LayoutInflater.from(context), this, true)

    lateinit var noteData: Note

    /* === LISTENERS === */
    lateinit var listener: NoteSwapListener

    /* === Access attributes === */
    var title: String? = ""
        set(value) {
            field = value
            updateTitle()
        }

    //Note description
    var text: String? = ""
        set(value) {
            field = value
            updateText()
        }

    //Can store: Drawable? | Bitmap?
    private var coverImage: Any? = null
        set(value) {
            field = value
            setCoverImageResource()
        }

    init {
        prepareView(attrs)
        configureEventListeners()
    }

    //Handle component init state
    private fun prepareView(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.NoteSwapView)

            //Manage attrs
            title = attributes.getString(R.styleable.NoteSwapView_note_title)
            text = attributes.getString(R.styleable.NoteSwapView_note_text)
            coverImage = attributes.getDrawable(R.styleable.NoteSwapView_note_image_drawable)

            attributes.recycle()
        }
    }

    private fun updateTitle() {
        binding.noteSwapViewTextViewItemTitle.text = title
    }

    private fun updateText() {
        binding.noteSwapViewTextViewItemDescription.text = text
    }

    private fun setCoverImageResource() {
        if (coverImage == null)
            binding.noteSwapViewImageViewNoteImage.setImageDrawable(context.findDrawable(R.drawable.sample_image))

        if (coverImage is Drawable) {
            binding.noteSwapViewImageViewNoteImage.setImageDrawable(coverImage as Drawable)
        }

        if (coverImage is Bitmap) {
            binding.noteSwapViewImageViewNoteImage.setImageBitmap(coverImage as Bitmap)
        }
    }

    fun setCoverImageBitmap(bitmap: Bitmap?) {
        this.coverImage = bitmap
    }

    fun setCoverImageUri(uri: String?) {
        if (uri != null)
            this.coverImage = uri
    }

    //Configure event listeners
    @SuppressLint("ClickableViewAccessibility")
    private fun configureEventListeners() {
        /**
         * This section handle the user touch input like swap, touch and drag action.
         * dragValue = Drag force (dp to move container after drag)
         * startX = Initial point of touch
         * absoluteDragX = Amount of pixels shifted during the swap
         * isDragged = Object is moved?
         */
        val dragValue = 60f.toDp(context)
        var startX = 0f
        var absoluteDragX = 0f
        var isDragged = false

        //Set delete button action
        binding.noeSwapViewImageViewDeleteButton.setOnClickListener {

            //Notify delete listener
            listener.onItemDelete(noteData)
        }

        //Handle swap
        binding.NoteSwapViewFrameLayoutSwapCard.setOnTouchListener { view, event ->
            /**
             * What this works?
             *
             * ACTION_DOWN: When user touch in the container for the first time "startX" hold the position of touch to calculate
             * absoluteDragX after
             *
             * ACTION_MOVE: Calc. drag amount using current touch position (event.x) minus start position (startX)
             *
             * ACTION_UP: If dragX is less than 31 and greater than -31 means the user clicked instead of
             * moving the container to swap, this action just notifies onItemSelect listener and reset the default drag values
             * for next touch action. Otherwise, based on dragX move the container above to left or right using view animations
             * and reset the default drag values
             *
             * ->> ACTION_CANCEL: DUPLICATED CODE OF ACTION_UP <<-
             * Not handle this action causes drag bugs if user swap outside container.
             */
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                }
                MotionEvent.ACTION_UP -> {
                    if (absoluteDragX <= 30 && absoluteDragX >= -30) {
                        listener.onItemSelect(this, noteData)

                        startX = 0f
                        absoluteDragX = 0f

                        return@setOnTouchListener true
                    }

                    if (absoluteDragX >= 60 && !isDragged) {
                        view.animate()
                            .x(dragValue)
                            .setDuration(150)
                            .start()

                        isDragged = true
                    } else if (absoluteDragX <= -40) {
                        view.animate()
                            .x(0f)
                            .setDuration(400)
                            .start()

                        isDragged = false
                    }

                    //Reset values
                    startX = 0f
                    absoluteDragX = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    absoluteDragX = event.x - startX
                }
                MotionEvent.ACTION_CANCEL -> {
                    println("X $absoluteDragX")

                    if (absoluteDragX <= 30 && absoluteDragX >= -30) {
                        startX = 0f
                        absoluteDragX = 0f

                        return@setOnTouchListener true
                    }

                    if (absoluteDragX >= 60 && !isDragged) {
                        view.animate()
                            .x(dragValue)
                            .setDuration(150)
                            .start()

                        isDragged = true
                    } else if (absoluteDragX <= -40) {
                        view.animate()
                            .x(0f)
                            .setDuration(400)
                            .start()

                        isDragged = false
                    }

                    //Reset values
                    startX = 0f
                    absoluteDragX = 0f
                }
            }
            true
        }
    }

    interface NoteSwapListener {
        fun onItemSelect(noteView: NoteSwapView, noteData: Note)
        fun onItemDelete(noteData: Note)
    }
}
