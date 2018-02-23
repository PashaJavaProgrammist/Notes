package com.dev.pavelharetskiy.notes_kotlin.views

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.dialogs.DelOrChangeDialog
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notes_card_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import com.dev.pavelharetskiy.notes_kotlin.dialogs.PhotoDialog
import android.widget.ImageView
import android.widget.TextView
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import com.dev.pavelharetskiy.notes_kotlin.orm.updateNote


class NotesHolder(private val cardView: CardView, val context: Context) : RecyclerView.ViewHolder(cardView) {

    @SuppressLint("SimpleDateFormat")
    private var sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

    fun updateHolder(note: Note?) {
        setListeners(cardView)
        cardView.tvTitle.text = note?.title
        cardView.tvBody.text = note?.body
        if (note != null) {
            cardView.tvTime.text = sdf.format(Date(note.date))
        }
        cardView.tvIdInLL.text = note?.id.toString()
        if (note?.isFavorite == 1) {
            cardView.iv_isFav.setBackgroundResource(android.R.drawable.btn_star_big_on)
        } else if (note?.isFavorite == 0) {
            cardView.iv_isFav.setBackgroundResource(android.R.drawable.btn_star_big_off)
        }
        if (note != null) {
            cardView.iv_isFav.contentDescription = Integer.toString(note.id)
        }
        if (note != null) {
            cardView.ivPhoto_on_card.contentDescription = Integer.toString(note.id)
        }
        val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim)
        cardView.iv_isFav.startAnimation(anim)
        var uri: Uri? = null
        if (note?.uri != null) {
            uri = Uri.parse(note.uri)
        }
        Picasso.with(context)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher)
                .resize(256, 144)
                .error(R.mipmap.ic_launcher)
                .into(cardView.ivPhoto_on_card)
    }

    private fun setListeners(cardView: CardView) {
        cardView.linLayTVs.setOnClickListener { onClickNote(cardView.linLayTVs) }
        cardView.iv_isFav.setOnClickListener { onClickFav(cardView.iv_isFav) }
        cardView.ivPhoto_on_card.setOnClickListener { onClickPhoto(cardView.ivPhoto_on_card) }
    }


    private fun onClickNote(view: View) {
        val id = Integer.parseInt((view.findViewById<View>(R.id.tvIdInLL) as TextView).text.toString())
        val delOrChangeDialog = DelOrChangeDialog()
        delOrChangeDialog.idNote = id
        delOrChangeDialog.show((context as MainActivity).supportFragmentManager, null)
    }

    private fun onClickFav(imageView: ImageView) {
        val idNote = Integer.parseInt(imageView.getContentDescription() as String)
        val note = getNoteById(idNote)
        if (note?.isFavorite == 1) note.isFavorite = 0
        else if (note?.isFavorite == 0) note.isFavorite = 1
        updateNote(note)
        (context as MainActivity).updateScreen()
    }

    private fun onClickPhoto(imageView: ImageView) {
        val id = Integer.parseInt(imageView.getContentDescription() as String)
        val photoDialog = PhotoDialog()
        photoDialog.idNote = id
        photoDialog.show((context as MainActivity).supportFragmentManager, null)
    }
}

