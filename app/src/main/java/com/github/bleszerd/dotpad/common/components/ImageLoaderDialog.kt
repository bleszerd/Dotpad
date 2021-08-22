package com.github.bleszerd.dotpad.common.components

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import com.github.bleszerd.dotpad.common.constants.Constants
import com.github.bleszerd.dotpad.common.constants.Constants.MimeType
import com.github.bleszerd.dotpad.databinding.ImageLoaderDialogBinding
import java.io.File
import android.os.Environment
import java.lang.Exception
import android.widget.Toast

import android.graphics.Bitmap

import android.content.ContentResolver
import android.widget.ImageView
import androidx.core.content.FileProvider


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
    private lateinit var imageUri: Uri

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
        var photo: File? = null

        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            //Create temporary file for image
            val tempDir = context.getExternalFilesDir("/temp")

            //If temp dir no exists create there
            if (!tempDir?.exists()!!){
                tempDir.mkdir()
            }

            //Create temp file inside temp dir
            photo = File.createTempFile("temp_picture", ".jpg", tempDir)

            //Override if image already exists
            photo?.delete()
        } catch (e: Error) {
            e.printStackTrace()
        }

        // If temp file exists
        if (photo != null) {
            //Get image uri with file provider
            imageUri =
                FileProvider.getUriForFile(context, "com.github.bleszerd.dotpad.provider", photo)

            //Put output image to extras
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            listener.onOpenGalleryAction(i, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    //Get captured camera image
    fun grabImage(context: Context): Bitmap? {
        context.contentResolver.notifyChange(imageUri, null)

        try {
            //Get image from media store with image uri
            return MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } catch (e: Exception) {
            Toast.makeText(context, "Falha ao ler imagem da câmera", Toast.LENGTH_SHORT).show()
        }

        return null
    }

    //Set listener
    fun addListener(listener: ImageDialogListener): ImageLoaderDialog {
        this.listener = listener

        return this
    }

    // /storage/emulated/0/.temp

    interface ImageDialogListener {
        fun onConfirmWebUrl(url: String) {}
        fun onOpenGalleryAction(intent: Intent, requestCode: Int) {}
        fun onOpenCameraAction(intent: Intent, requestCode: Int)
    }
}