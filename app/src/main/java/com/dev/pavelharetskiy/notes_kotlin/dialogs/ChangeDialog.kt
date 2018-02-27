package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import android.widget.TextView
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import com.dev.pavelharetskiy.notes_kotlin.orm.updateNote
import kotlinx.android.synthetic.main.fragment_create_dialog.view.*


class ChangeDialog : DialogFragment() {
    private var idNote = -1
    private val instId = "idTOsave"

    private var edTitle: TextView? = null
    private var edBody: TextView? = null

    private fun onClickYes() {
        val noteToChange: Note?
        val title = edTitle?.text.toString()
        val body = edBody?.text.toString()
        if (title.isNotEmpty()) {
            noteToChange = getNoteById(idNote)
            noteToChange?.body = body
            noteToChange?.title = title
            updateNote(noteToChange)
            Toast.makeText(activity, "Note is changed", Toast.LENGTH_SHORT).show()
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

    fun setId(id: Int) {
        this.idNote = id
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(instId, idNote)
    }

    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            idNote = savedInstanceState.getInt(instId)
            updateViews()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.change_note)
        val v = inflater.inflate(R.layout.fragment_create_dialog, container, false)
        v.btYesCreate.setOnClickListener { onClickYes() }
        v.btNoCreate.setOnClickListener { onClickNo() }
        edTitle = v.edTitleCreate
        edBody = v.edBodyCreate
        updateViews()
        return v
    }

    private fun updateViews() {
        if (idNote != -1) {
            val noteForChange = getNoteById(idNote)
            edTitle?.text = noteForChange?.title
            edBody?.text = noteForChange?.body
        }
    }
}