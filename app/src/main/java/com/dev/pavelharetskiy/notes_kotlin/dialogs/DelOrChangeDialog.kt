package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.annotation.Nullable
import android.view.View
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.orm.DBFlowNoteRepository
import android.widget.TextView
import com.dev.pavelharetskiy.notes_kotlin.R
import kotlinx.android.synthetic.main.fragment_del_change_dialog.view.*


class DelOrChangeDialog : DialogFragment() {
    var idNote: Int = 0
    private val INSTID = "idtosave"

    private var tvDelete: TextView? = null
    private var tvChange: TextView? = null
    private var tvExit: TextView? = null

    private fun onClickExit() {
        this.dismiss()
    }

    private fun onClickDelete() {
        DBFlowNoteRepository.deleteNoteById(idNote)
        if (activity != null) {
            (activity as MainActivity).setListNotes()
        }
        Toast.makeText(activity, "Note is deleted", Toast.LENGTH_SHORT).show()
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
        outState.putInt(INSTID, idNote)
    }

    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            idNote = savedInstanceState.getInt(INSTID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.choose_action)
        val v = inflater.inflate(R.layout.fragment_del_change_dialog, container, false)
        tvDelete = v.tvDelete
        tvDelete?.setOnClickListener {
            onClickDelete()
        }
        tvChange = v.tvChange
        tvChange?.setOnClickListener {
            onClickChange()
        }
        tvExit = v.tvExit
        tvExit?.setOnClickListener {
            onClickExit()
        }
        return v
    }

}
