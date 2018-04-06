package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import com.dev.pavelharetskiy.notes_kotlin.orm.updateNote
import kotlinx.android.synthetic.main.fragment_create_dialog.view.*

class ChangeDialog : DialogFragment() {

    private var idNote = -1
    private val bundleKeyID by lazy { getString(R.string.dialog_id) }

    private lateinit var edTitle: TextView
    private lateinit var edBody: TextView

    private fun onClickYes() {
        val noteToChange: Note?
        val title = edTitle.text.toString()
        val body = edBody.text.toString()
        if (title.isNotEmpty()) {
            noteToChange = getNoteById(idNote)
            noteToChange?.body = body
            noteToChange?.title = title
            updateNote(noteToChange)
            Toast.makeText(activity, getString(R.string.note_is_changed), Toast.LENGTH_SHORT).show()
            (activity as MainActivity).updateScreen()
            this.dismiss()
        } else {
            Toast.makeText(activity, getString(R.string.title_not_empty), Toast.LENGTH_SHORT).show()
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
        outState.putInt(bundleKeyID, idNote)
    }

    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (idNote == -1) {
            idNote = savedInstanceState?.getInt(bundleKeyID) ?: -1
        }
        updateViews()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.change_note)
        val v = inflater.inflate(R.layout.fragment_create_dialog, container, false).apply {
            btYesCreate.setOnClickListener { onClickYes() }
            btNoCreate.setOnClickListener { onClickNo() }
            edTitle = edTitleCreate
            edBody = edBodyCreate
        }
        updateViews()
        return v
    }

    private fun updateViews() {
        if (idNote != -1) {
            val noteForChange = getNoteById(idNote)
            edTitle.text = noteForChange?.title
            edBody.text = noteForChange?.body
        }
    }
}