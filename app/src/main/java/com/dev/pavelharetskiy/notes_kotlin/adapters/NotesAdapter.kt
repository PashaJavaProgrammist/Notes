package com.dev.pavelharetskiy.notes_kotlin.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.views.NotesHolder

class NotesAdapter(private var notesList: List<Note>) : RecyclerView.Adapter<NotesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        val context: Context = parent.context
        val cv: CardView = (LayoutInflater.from(context).inflate(R.layout.notes_card_view, parent, false) as CardView)
        return NotesHolder(cv, context)
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.updateHolder(notesList[position])
    }

    override fun getItemCount() = notesList.size

    fun setNoteList(notesList: List<Note>) {
        this.notesList = notesList
        notifyDataSetChanged()
    }
}