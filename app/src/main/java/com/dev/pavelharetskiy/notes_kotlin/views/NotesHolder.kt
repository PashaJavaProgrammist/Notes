package com.dev.pavelharetskiy.notes_kotlin.views

import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.activities.MainActivity
import com.dev.pavelharetskiy.notes_kotlin.dialogs.DelOrChangeDialog
import com.dev.pavelharetskiy.notes_kotlin.dialogs.PhotoDialog
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import com.dev.pavelharetskiy.notes_kotlin.orm.updateNote
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notes_card_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotesHolder(private val cardView: CardView, val context: Context) : RecyclerView.ViewHolder(cardView) {

    private var sdf: SimpleDateFormat = SimpleDateFormat(context.getString(R.string.format), Locale.getDefault())

    fun updateHolder(note: Note?) {
        setListeners(cardView)
        var uri: Uri? = null
        val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_anim)

        cardView.apply {
            note?.also {
                if (it.uri != null) {
                    uri = Uri.parse(it.uri)
                }
                tvTitle.text = it.title
                tvBody.text = it.body
                tvIdInLL.text = it.id.toString()
                tvTime.text = sdf.format(Date(it.date))
                ivPhoto_on_card.contentDescription = Integer.toString(it.id)
                tvIdInLL.text = it.id.toString()
                iv_isFav.apply {
                    contentDescription = Integer.toString(it.id)
                    when (it.isFavorite) {
                        0 -> setBackgroundResource(android.R.drawable.btn_star_big_off)
                        1 -> setBackgroundResource(android.R.drawable.btn_star_big_on)
                    }
                }
                iv_isFav.startAnimation(anim)
            }
        }

        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.no_photo)
                .resize(256, 256)
                .error(R.drawable.error)
                .centerCrop()
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
        getNoteById(Integer.parseInt(imageView.contentDescription as String))?.also {
            when (it.isFavorite) {
                0 -> it.isFavorite = 1
                1 -> it.isFavorite = 0
            }
            updateNote(it)
        }
        (context as MainActivity).updateScreen()
    }

    private fun onClickPhoto(imageView: ImageView) {
        val photoDialog = PhotoDialog()
        photoDialog.idNote = Integer.parseInt(imageView.contentDescription as String)
        photoDialog.show((context as MainActivity).supportFragmentManager, null)
    }
}