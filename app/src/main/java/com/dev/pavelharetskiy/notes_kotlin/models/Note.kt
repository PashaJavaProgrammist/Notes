package com.dev.pavelharetskiy.notes_kotlin.models

import com.dev.pavelharetskiy.notes_kotlin.orm.NoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(name = "notes", database = NoteDatabase::class)
data class Note(@Column(name = "Id")
                @PrimaryKey(autoincrement = true)
                var id: Int = 0,
                @Column(name = "Title")
                var title: String? = null,
                @Column(name = "Body")
                var body: String? = null,
                @Column(name = "Uri")
                var uri: String? = null,
                @Column(name = "Date")
                var date: Long = 0,
                @Column(name = "IsFav")
                var isFavorite: Int = 0) : BaseModel() {

    fun getIsFavorite(): Int {
        return isFavorite
    }//dont't compile without this (Why???)

    fun setIsFavorite(isFavorite: Int) {
        this.isFavorite = isFavorite
    }//dont't compile without this (Why???)

    companion object {
        val FAVORITE = 1
    }

    constructor(title: String?, body: String, date: Long) : this() {
        this.title = title
        this.body = body
        this.date = date
    }


}