package com.dev.pavelharetskiy.notes_kotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.adapters.NotesAdapter
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.fragment_notes.view.*

class NotesFragment : Fragment() {

    private var notesAdapter: NotesAdapter? = null
    private lateinit var noteList: List<Note>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_notes, container, false).apply {
                recyclerViewNotesFragment?.layoutManager = LinearLayoutManager(activity)
                recyclerViewNotesFragment?.adapter = notesAdapter
            }


    fun setNoteList(noteList: List<Note>) {
        this.noteList = noteList
        notesAdapter = NotesAdapter(noteList).apply {
            setNoteList(noteList)
        }
    }

    fun updateNoteList(noteList: List<Note>) {
        this.noteList = noteList
        isNotesOnScreen(noteList)
        notesAdapter?.setNoteList(noteList)
    }

    private fun isNotesOnScreen(noteList: List<Note>) {
        if (noteList.isEmpty()) {
            no_notes.visibility = View.VISIBLE
        } else {
            no_notes.visibility = View.GONE
        }
    }
}