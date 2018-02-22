package com.dev.pavelharetskiy.notes_kotlin.dialogs

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.support.annotation.Nullable
import android.view.View
import android.widget.Toast
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import android.widget.LinearLayout
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import kotlinx.android.synthetic.main.fragment_photo_dialog.view.*

class PhotoDialog : DialogFragment() {
    var idNote: Int = 0
    private val instId = "idsave"

    private var btMake: LinearLayout? = null
    private var btAdd: LinearLayout? = null
    private var btShow: LinearLayout? = null
    private var btExit: LinearLayout? = null
    private var btDeletePhoto: LinearLayout? = null

    private fun onClickMake() {
        if (activity != null) {
            (activity as MainActivity).startCameraActivity(idNote)
        }
        this.dismiss()
    }

    private fun onClickAdd() {
        if (activity != null) {
            (activity as MainActivity).startPickPhotoActivity(idNote)
        }
        this.dismiss()
    }

    private fun onClickDeletePhoto() {
        if (activity != null) {
            (activity as MainActivity).delPhotoNote(idNote)
        }
        this.dismiss()
    }

    private fun onClickShow() {
        val uri = getNoteById(idNote)?.uri
        try {
            if (uri != "") {
                if (activity != null) {
                    (activity as MainActivity).startActivityDetail(idNote)
                }
            }
            this.dismiss()
        } catch (ex: Exception) {
            Toast.makeText(activity, "Nothing", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onClickExit() {
        this.dismiss()
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(instId, idNote)
    }

    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            idNote = savedInstanceState.getInt(instId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.setTitle(R.string.choose_action)
        val v = inflater.inflate(R.layout.fragment_photo_dialog, container, false)
        btMake = v.btMake
        btMake?.setOnClickListener { onClickMake() }
        btAdd = v.btAdd
        btAdd?.setOnClickListener { onClickAdd() }
        btShow = v.btShow
        btShow?.setOnClickListener { onClickShow() }
        btExit = v.btExit
        btExit?.setOnClickListener { onClickExit() }
        btDeletePhoto = v.btDeletePhoto
        btDeletePhoto?.setOnClickListener { onClickDeletePhoto() }
        return v
    }
}