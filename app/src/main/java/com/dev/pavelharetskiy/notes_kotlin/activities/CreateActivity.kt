package com.dev.pavelharetskiy.notes_kotlin.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.orm.createNote
import kotlinx.android.synthetic.main.fragment_create_dialog.*

class CreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_create_dialog)

        btYesCreate.setOnClickListener {
            onClickCreate()
        }
        btNoCreate.setOnClickListener {
            onClickCancel()
        }
    }

    private fun onClickCreate() {
        val titleNote = edTitleCreate.text.toString()
        val bodyNote = edBodyCreate.text.toString()
        if (titleNote.isNotEmpty()) {
            createNote(titleNote, bodyNote)
            finishAffinity()
        } else {
            Toast.makeText(this, getString(R.string.title_not_empty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickCancel() {
        finishAffinity()
    }
}
