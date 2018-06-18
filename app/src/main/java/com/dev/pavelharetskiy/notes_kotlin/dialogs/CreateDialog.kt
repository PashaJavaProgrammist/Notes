package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.orm.createNote
import kotlinx.android.synthetic.main.fragment_create_dialog.*
import kotlinx.android.synthetic.main.fragment_create_dialog.view.*

class CreateDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.create_note)
        return inflater.inflate(R.layout.fragment_create_dialog, container, false).apply {
            btYesCreate.setOnClickListener {
                onClickYes()
            }
            btNoCreate.setOnClickListener {
                onClickNo()
            }
        }
    }

    private fun onClickYes() {
        val titleNote = edTitleCreate.text.toString()
        val bodyNote = edBodyCreate.text.toString()
        if (titleNote.isNotEmpty()) {
            createNote(titleNote, bodyNote)
            (activity as MainActivity).updateScreen()
            this.dismiss()
        } else {
            Toast.makeText(activity, getString(R.string.title_not_empty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickNo() {
        this.dismiss()
    }

}