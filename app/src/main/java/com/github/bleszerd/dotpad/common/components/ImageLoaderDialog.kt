package com.github.bleszerd.dotpad.common.components

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.constants.Constants.MimeType
import com.github.bleszerd.dotpad.databinding.ImageLoaderDialogBinding

/**
Dotpad
16/08/2021 - 10:30
Created by bleszerd.
@author alive2k@programmer.net
 */
class ImageLoaderDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    companion object {
        const val SELECT_PICTURE_GALLERY_REQUEST_CODE = 1
        const val TAKE_PICTURE_REQUEST_CODE = 2
    }

    private val binding = ImageLoaderDialogBinding.inflate(LayoutInflater.from(context))

    private lateinit var listener: ImageDialogListener
    private lateinit var inputUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCanceledOnTouchOutside(true)
        handleSelectFromWebUrlButton()
        handleSelectFromGalleryButton()
        handleTakePictureButton()
        handleUrlConfirmButton()

        setContentView(binding.root)
    }

    //When user clicks on "Select image from web"
    private fun handleSelectFromWebUrlButton() {
        binding.imageLoaderDialogLinearLayoutImageFromWebButton.setOnClickListener {
            toggleWebUrlInputContentVisibility()
            dismiss()
        }
    }

    //When user clicks on "Select image from gallery"
    private fun handleSelectFromGalleryButton() {
        binding.imageLoaderDialogLinearLayoutImageFromGalleryButton.setOnClickListener {
            openGalleryToSelectPicture()
            dismiss()
        }
    }

    //When user clicks on "Take a picture"
    private fun handleTakePictureButton() {
        binding.imageLoaderDialogLinearLayoutTakePictureButton.setOnClickListener {
            openCameraToTakePicture()
            dismiss()
        }
    }

    //User put the image URL from web and click in "Ok"
    private fun handleUrlConfirmButton() {
        binding.imageLoaderDialogTextViewOkayButton.setOnClickListener {

            //Save input url
            inputUrl = binding.imageLoaderDialogEditTextImageUrlInput.text.toString()

            //Notify Listener confirm URL
            listener.onConfirmWebUrl(inputUrl)

            //Hide dialog
            dismiss()
        }
    }

    //Toggle web url input visibility
    //Visible -> Hide    Invisible -> Show
    private fun toggleWebUrlInputContentVisibility() {
        val urlInputContent = binding.imageLoaderDialogLinearLayoutWebUrlInputContent

        if (urlInputContent.visibility == View.VISIBLE) {
            urlInputContent.visibility = View.GONE
            return
        }

        urlInputContent.visibility = View.VISIBLE
    }

    //Create intent to open gallery and notify listener
    private fun openGalleryToSelectPicture() {
        val i = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = MimeType.ALL_IMAGES
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        listener.onOpenGalleryAction(i, SELECT_PICTURE_GALLERY_REQUEST_CODE)
    }

    //Create intent to open camera and notify listener
    private fun openCameraToTakePicture() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        listener.onOpenGalleryAction(i, TAKE_PICTURE_REQUEST_CODE)
    }

    //Set listener
    fun addListener(listener: ImageDialogListener): ImageLoaderDialog {
        this.listener = listener

        return this
    }

    interface ImageDialogListener {
        fun onConfirmWebUrl(url: String) {}
        fun onOpenGalleryAction(intent: Intent, requestCode: Int) {}
        fun onOpenCameraAction(intent: Intent, requestCode: Int)
    }
}