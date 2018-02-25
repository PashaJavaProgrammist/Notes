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
        var uri: Uri? = null
        val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim)

        cardView.apply {
            if (note != null) {
                if (note.uri != null) {
                    uri = Uri.parse(note.uri)
                }
                tvTitle.text = note.title
                tvBody.text = note.body
                tvIdInLL.text = note.id.toString()
                tvTime.text = sdf.format(Date(note.date))
                ivPhoto_on_card.contentDescription = Integer.toString(note.id)
                tvIdInLL.text = note.id.toString()
                iv_isFav.apply {
                    contentDescription = Integer.toString(note.id)
                    when (note.isFavorite) {
                        0 -> setBackgroundResource(android.R.drawable.btn_star_big_off)
                        1 -> setBackgroundResource(android.R.drawable.btn_star_big_on)
                    }
                }
                iv_isFav.startAnimation(anim)
            }
        }

        Picasso.with(context)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher)
                .resize(256, 144)
                .error(R.mipmap.ic_launcher)
                .into(cardView.ivPhoto_on_card)
    }

    private fun setListeners(cardView: CardView) {
        cardView.apply {
            linLayTVs.setOnClickListener { onClickNote(linLayTVs) }
            iv_isFav.setOnClickListener { onClickFav(iv_isFav) }
            ivPhoto_on_card.setOnClickListener { onClickPhoto(ivPhoto_on_card) }
        }
    }


    private fun onClickNote(view: View) {
        val delOrChangeDialog = DelOrChangeDialog()
        delOrChangeDialog.idNote = Integer.parseInt((view.findViewById<View>(R.id.tvIdInLL) as TextView).text.toString())
        delOrChangeDialog.show((context as MainActivity).supportFragmentManager, null)
    }

    private fun onClickFav(imageView: ImageView) {
        val note = getNoteById(Integer.parseInt(imageView.contentDescription as String))
        when (note?.isFavorite) {
            0 -> note.isFavorite = 1
            1 -> note.isFavorite = 0
        }
        updateNote(note)
        (context as MainActivity).updateScreen()
    }

    private fun onClickPhoto(imageView: ImageView) {
        val photoDialog = PhotoDialog()
        photoDialog.idNote = Integer.parseInt(imageView.getContentDescription() as String)
        photoDialog.show((context as MainActivity).supportFragmentManager, null)
    }
}