package com.github.bleszerd.dotpad.common.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.github.bleszerd.dotpad.databinding.AddViewDialogBinding

class AddViewDialog: Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    private val binding = AddViewDialogBinding.inflate(LayoutInflater.from(context))
    private lateinit var listener: AddViewDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setCanceledOnTouchOutside(true)
        handleSelectText()
        handleSelectImage(context)

        setContentView(binding.root)
    }

    private fun handleSelectImage(context: Context) {
        binding.addViewDialogLinearLayoutImageButton.setOnClickListener {
            listener.onImageSelected(context)
            dismiss()
        }
    }

    private fun handleSelectText() {
        binding.addViewDialogLinearLayoutTextButton.setOnClickListener {
            listener.onTextSelected()
            dismiss()
        }
    }

    fun addListener(listener: AddViewDialogListener): AddViewDialog {
        this.listener = listener

        return this
    }


    interface AddViewDialogListener {
        fun onTextSelected()
        fun onImageSelected(context: Context)
    }
}