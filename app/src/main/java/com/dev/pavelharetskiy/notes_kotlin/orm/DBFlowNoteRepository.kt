package com.dev.pavelharetskiy.notes_kotlin.orm

import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.raizlabs.android.dbflow.sql.language.Select

object DBFlowNoteRepository {

    //private val denominator = 1000

    fun getAllNotes(): List<Note>? {
        return Select().from(Note::class.java).queryList()
    }

    fun getFavoriteNotes(): List<Note>? {
        //   return Select().from(Note::class.java).where(Note_Table.IsFav.eq(Note.FAVORITE)).queryList()
        return getAllNotes()?.filter { note -> note.isFavorite == Note.FAVORITE }
    }

    fun getNoteById(id: Int): Note? {
        // return Select().from(Note::class.java).where(Note_Table.Id.`is`(id)).querySingle()
        return getAllNotes()?.filter { note -> note.id == id }?.get(0)
    }

    fun updateNote(note: Note?) {
        note?.save()
    }

    fun deleteNoteById(id: Int) {
        getNoteById(id)?.delete()
    }

    fun createNote(note: Note) {
        // note.id = System.currentTimeMillis().toInt() / denominator
        note.save()
    }
}
