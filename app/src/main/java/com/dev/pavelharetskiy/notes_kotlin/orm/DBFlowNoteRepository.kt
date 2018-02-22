package com.dev.pavelharetskiy.notes_kotlin.orm

import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.models.Note_Table
import com.raizlabs.android.dbflow.sql.language.Select

    fun getAllNotes(): List<Note> {
        return Select().from(Note::class.java).queryList()
    }

    fun getFavoriteNotes(): List<Note> {
        return Select().from(Note::class.java).where(Note_Table.IsFav.eq(Note.FAVORITE)).queryList()
    }

    fun getNoteById(id: Int): Note? {
        return Select().from(Note::class.java).where(Note_Table.Id.`is`(id)).querySingle()
    }

    fun updateNote(note: Note?) {
        note?.save()
    }

    fun deleteNoteById(id: Int) {
        getNoteById(id)?.delete()
    }

    fun createNote(title: String?, body: String) {
        val note = Note(title, body, System.currentTimeMillis())
        note.save()
    }

