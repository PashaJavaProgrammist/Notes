package com.dev.pavelharetskiy.notes_kotlin.activities

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.SharedPreferences
import android.net.Uri
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.fragments.NotesFragment
import java.io.File
import android.content.Intent
import android.provider.Settings
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.orm.DBFlowNoteRepository
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.provider.MediaStore
import android.widget.Toast
import android.os.Environment
import com.dev.pavelharetskiy.notes_kotlin.dialogs.CreateDialog


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_CODE_PHOTO = 4554
    private val REQUEST_CODE_PICK_PHOTO = 4124

    private var fragment: NotesFragment? = null
    private var isFavOnScreen = false
    private var uri: Uri? = null
    private var idToChangePhoto = -1
    private var directory: File? = null
    private lateinit var spref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spref = getPreferences(MODE_PRIVATE)
        if (spref.contains("idToChange")) idToChangePhoto = spref.getInt("idToChange", -1)
        if (spref.contains("uri")) uri = Uri.parse(spref.getString("uri", ""))

        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            CreateDialog().show(supportFragmentManager, null)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val openSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + packageName))
                startActivity(openSettingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: NotesFragment? =null
        val notesList: List<Note>?
        when (item.itemId) {
            R.id.nav_all_notes -> {
                notesList = DBFlowNoteRepository.getAllNotes()
                fragment?.setNoteList(notesList)
                isFavOnScreen = false
            }
            R.id.nav_favorite_notes -> {
                notesList = DBFlowNoteRepository.getFavoriteNotes()
                fragment?.setNoteList(notesList)
                isFavOnScreen = true
            }
            else -> isFavOnScreen = false
        }
        doTransaction(fragment)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean("isFavorite", isFavOnScreen)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            isFavOnScreen = savedInstanceState.getBoolean("isFavorite", false)
        }
    }

    override fun onResume() {
        super.onResume()
        val fragment = NotesFragment()
        try {
            if (!isFavOnScreen) {
                fragment.setNoteList(DBFlowNoteRepository.getAllNotes())
            } else if (isFavOnScreen) {
                fragment.setNoteList(DBFlowNoteRepository.getFavoriteNotes())
            }
        } catch (ex: Exception) {
            //error
        }

        doTransaction(fragment)
    }

    override fun onStop() {
        super.onStop()
        val ed = spref.edit()
        if (idToChangePhoto != -1) ed.putInt("idToChange", idToChangePhoto)
        if (uri != null) ed.putString("uri", uri?.getPath())
        ed.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {//возврат результата от камеры
        if (requestCode == REQUEST_CODE_PHOTO) {//результат с камеры
            if (resultCode == Activity.RESULT_OK) {
                if (resultIntent == null) {
                    //добавлеие
                    try {
                        val note = DBFlowNoteRepository.getNoteById(idToChangePhoto)
                        val uriStr: String
                        if (uri.toString()[0] == '/') {
                            uriStr = "file://" + uri.toString()
                        } else {
                            uriStr = uri.toString()
                        }

                        note?.uri = uriStr
                        DBFlowNoteRepository.updateNote(note)
                        setListNotes()
                    } catch (ex: Exception) {
                        //
                    }

                }
            }
        } else if (requestCode == REQUEST_CODE_PICK_PHOTO) {//возврат результата от выбора фото
            if (resultCode == Activity.RESULT_OK) {
                val note = DBFlowNoteRepository.getNoteById(idToChangePhoto)
                if (resultIntent != null) {
                    val imageUri = resultIntent.data
                    if (imageUri != null) {
                        note?.uri = imageUri.toString()
                        DBFlowNoteRepository.updateNote(note)
                        setListNotes()
                    }
                }
            }
        }
    }

    //===================================================//
    //Metods
    //===================================================//

    fun doTransaction(fragment: NotesFragment?) {
        try {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frameForFragments, fragment)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.commit()
        } catch (ex: Exception) {
            //Error
        }

    }

    fun setListNotes() {
        try {
            var notes: List<Note>? = null
            if (isFavOnScreen) {
                notes = DBFlowNoteRepository.getFavoriteNotes()
            } else if (!isFavOnScreen) {
                notes = DBFlowNoteRepository.getAllNotes()
            }
            if (notes != null && fragment != null) {
                fragment?.setNoteList(notes)
            }
        } catch (ex: Exception) {
            //
        }

    }

    fun startActivityDetail(id: Int) {

        //startActivity(new Intent(this, DetailActivity.class).putExtra("id", id));

        val uriPath = DBFlowNoteRepository.getNoteById(id)!!.uri
        if (uriPath != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriPath))
            startActivity(intent)
        } else {
            Toast.makeText(this, "There is no photo..", Toast.LENGTH_SHORT).show()
        }
    }

    fun startCameraActivity(id: Int) {
        generateFileUri()
        idToChangePhoto = id
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//запускаем камеру с помощью интента
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    fun delPhotoNote(id: Int) {
        val note = DBFlowNoteRepository.getNoteById(id)
        note?.uri = null
        DBFlowNoteRepository.updateNote(note)
        setListNotes()
    }

    fun startPickPhotoActivity(id: Int) {
        idToChangePhoto = id
        val photoAddIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoAddIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)

        startActivityForResult(photoAddIntent, REQUEST_CODE_PICK_PHOTO)
    }

    private fun generateFileUri() {//генерируем путь к фото
        createDirectory()
        val file = File(directory?.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg")
        uri = Uri.fromFile(file)
    }

    private fun createDirectory() {//создаем папку для фото
        val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "NotesApp")
        if (!directory.exists())

            directory.mkdirs()
    }

    fun updateScreen() {
        if (fragment != null) {
            try {
                if (!isFavOnScreen)
                    fragment?.setNoteList(DBFlowNoteRepository.getAllNotes())
                else if (isFavOnScreen)
                    fragment?.setNoteList(DBFlowNoteRepository.getFavoriteNotes())
            } catch (ex: Exception) {
                Toast.makeText(this, "error..", Toast.LENGTH_SHORT).show()
            }

        }
    }


}
