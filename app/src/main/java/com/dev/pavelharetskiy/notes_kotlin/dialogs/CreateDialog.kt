package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import android.widget.TextView
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.orm.createNote
import kotlinx.android.synthetic.main.fragment_create_dialog.view.*

class CreateDialog : DialogFragment() {

    private lateinit var edTitle: TextView
    private lateinit var edBody: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.create_note)
        val v = inflater.inflate(R.layout.fragment_create_dialog, container, false)
        edBody = v.edBodyCreate
        edTitle = v.edTitleCreate
        v.btYesCreate.setOnClickListener {
            onClickYes()
        }
        v.btNoCreate.setOnClickListener {
            onClickNo()
        }
        return v
    }

    private fun onClickYes() {
        val titleNote = edTitle.text.toString()
        val bodyNote = edBody.text.toString()
        if (titleNote.isNotEmpty()) {
            createNote(titleNote, bodyNote)
            if (activity != null) {
                (activity as MainActivity).updateScreen()
            }
            this.dismiss()
        } else {
            Toast.makeText(activity, "Title shouldn't be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickNo() {
        this.dismiss()
    }

}