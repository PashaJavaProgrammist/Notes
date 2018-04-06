package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.orm.deleteNoteById
import kotlinx.android.synthetic.main.fragment_del_change_dialog.view.*

class DelOrChangeDialog : DialogFragment() {

    var idNote = -1
    private val bundleKeyID by lazy { getString(R.string.dialog_id) }

    private fun onClickExit() {
        this.dismiss()
    }

    private fun onClickDelete() {
        if (idNote != -1) {
            deleteNoteById(idNote)
            (activity as MainActivity).updateScreen()
            Toast.makeText(activity, getString(R.string.note_is_deleted), Toast.LENGTH_SHORT).show()
        }
        this.dismiss()
    }

    private fun onClickChange() {
        val changeDialog = ChangeDialog()
        changeDialog.id = idNote
        changeDialog.show(fragmentManager, null)
        this.dismiss()
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

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.choose_action)
        return inflater.inflate(R.layout.fragment_del_change_dialog, container, false).apply {
            tvDelete.setOnClickListener {
                onClickDelete()
            }
            tvChange.setOnClickListener {
                onClickChange()
            }
            tvExit.setOnClickListener {
                onClickExit()
            }
        }
    }

}