package com.dev.pavelharetskiy.notes_kotlin.orm

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = NoteDatabase.NAME, version = NoteDatabase.VERSION, generatedClassSeparator = "_")
object NoteDatabase {
    const val NAME: String = "NotesDatabase"
    const val VERSION: Int = 1
}