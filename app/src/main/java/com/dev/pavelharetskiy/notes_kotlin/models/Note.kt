package com.dev.pavelharetskiy.notes_kotlin.models

import com.dev.pavelharetskiy.notes_kotlin.orm.NoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(name = "notes", database = NoteDatabase::class)
class Note : BaseModel {

    companion object {
        val FAVORITE = 1
    }

    @Column(name = "Id")
    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(name = "Title")
    var title: String? = null

    @Column(name = "Body")
    var body: String? = null

    @Column(name = "Uri")
    var uri: String? = null

    @Column(name = "Date")
    var date: Long = 0

    @Column(name = "IsFav")
    var isFav: Int = 0

    constructor() {}

    constructor(title: String, body: String, date: Long, isFav: Int = 0) {
        this.title = title
        this.body = body
        this.date = date
        this.isFav = isFav
    }

}