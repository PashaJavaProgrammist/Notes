package com.dev.pavelharetskiy.notes_kotlin.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.dev.pavelharetskiy.notes_kotlin.R
import com.dev.pavelharetskiy.notes_kotlin.dialogs.CreateDialog
import com.dev.pavelharetskiy.notes_kotlin.fragments.NotesFragment
import com.dev.pavelharetskiy.notes_kotlin.models.Note
import com.dev.pavelharetskiy.notes_kotlin.orm.getListOfAllNotes
import com.dev.pavelharetskiy.notes_kotlin.orm.getListOfFavoriteNotes
import com.dev.pavelharetskiy.notes_kotlin.orm.getNoteById
import com.dev.pavelharetskiy.notes_kotlin.orm.updateNote
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val requestCodePhotoMake = 4554
    private val requestCodePhotoPick = 4124
    private val requestCodePerms = 41225

    private val codeStartCamera = 111
    private val codeStartDetail = 222
    private val codeStartPick = 333

    private var code = -1

    private lateinit var fragment: NotesFragment
    private var isFavOnScreen = false
    private var uri: Uri? = null
    private var idToChangePhoto = -1
    private lateinit var directory: File
    private lateinit var spref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        ActionBarDrawerToggle(
                this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close).apply {
            drawerlayout.addDrawerListener(this)
            syncState()
        }

        initSearchView()
        initPreferences()

        fab.setOnClickListener {
            CreateDialog().show(supportFragmentManager, null)
            Answers.getInstance().logCustom(CustomEvent(getString(R.string.new_note)))
        }

        nav_view.setNavigationItemSelectedListener(this)

        fragment = NotesFragment()
                .apply { setNoteList(if (isFavOnScreen) getListOfFavoriteNotes() else getListOfAllNotes()) }
                .also { doTransaction(it) }
    }

    override fun onResume() {
        super.onResume()
        updateScreen()
    }

    override fun onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startSettingsActivity() {
        val openSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName"))
        startActivity(openSettingsIntent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all_notes -> setListOfNotesInDrawer(false)
            R.id.nav_favorite_notes -> setListOfNotesInDrawer(true)
            else -> isFavOnScreen = false
        }

        drawerlayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setListOfNotesInDrawer(isListOfFavoriteNotes: Boolean) {
        isFavOnScreen = isListOfFavoriteNotes
        updateScreen()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.isFavorite), isFavOnScreen)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            isFavOnScreen = savedInstanceState.getBoolean(getString(R.string.isFavorite), false)
        }
    }

    override fun onStop() {
        super.onStop()
        val ed = spref.edit()
        if (idToChangePhoto != -1) {
            ed.putInt(getString(R.string.key_id), idToChangePhoto)
        }
        if (uri != null) {
            ed.putString(getString(R.string.uri_key), uri?.path)
        }
        if (code != -1) {
            ed.putInt(getString(R.string.key_code), code)
        }
        ed.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {//возврат результата от камеры
        if (requestCode == requestCodePhotoMake) {//результат с камеры
            if (resultCode == Activity.RESULT_OK) {
                //добавлеие
                val note = getNoteById(idToChangePhoto)
                val uriStr: String = if (uri.toString()[0] == '/') {
                    "file://${uri.toString()}"
                } else {
                    uri.toString()
                }

                note?.uri = uriStr
                updateNote(note)
                updateScreen()
            }
        } else if (requestCode == requestCodePhotoPick) {//возврат результата от выбора фото
            if (resultCode == Activity.RESULT_OK) {
                val note = getNoteById(idToChangePhoto)
                val imageUri = resultIntent?.data
                note?.uri = imageUri?.toString()
                updateNote(note)
                updateScreen()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodePerms -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, getString(R.string.was_granted), Toast.LENGTH_LONG).show()
                    when (code) {
                        codeStartCamera -> startCameraActivity(idToChangePhoto)
                        codeStartDetail -> startActivityDetail(idToChangePhoto)
                        codeStartPick -> startPickPhotoActivity(idToChangePhoto)
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getString(R.string.was_denied), Toast.LENGTH_LONG).show()

                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    //===================================================//
    //Metods
    //===================================================//

    private fun doTransaction(fragment: NotesFragment?) {
        val ft = supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameForFragments, fragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
        ft.commit()
    }

    fun startActivityDetail(id: Int) {
        code = codeStartDetail
        idToChangePhoto = id
        if (isPermissionGranted()) {
            val uriPath = getNoteById(id)?.uri
            if (uriPath != null) {
                val intent = Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(uriPath), getString(R.string.image_type))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show()
            }
        } else {
            checkPermissions()
        }
    }

    fun startCameraActivity(id: Int) {
        code = codeStartCamera
        idToChangePhoto = id
        if (isPermissionGranted()) {
            generateFileUri()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, requestCodePhotoMake)
        } else {
            checkPermissions()
        }
    }

    fun delPhotoNote(id: Int) {
        val note = getNoteById(id)
        note?.uri = null
        updateNote(note)
        updateScreen()
    }

    fun startPickPhotoActivity(id: Int) {
        code = codeStartPick
        idToChangePhoto = id
        if (isPermissionGranted()) {
            val photoAddIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(photoAddIntent, requestCodePhotoPick)
        } else {
            checkPermissions()
        }
    }

    private fun generateFileUri() {//генерируем путь к фото
        createDirectory()
        val file = File("${directory.path}/photo_${System.currentTimeMillis()}.jpg")
        uri /*= Uri.fromFile(file)

        val photoURI*/ = FileProvider.getUriForFile(
                this,
                getString(R.string.provider_pakage),
                file)
    }

    private fun createDirectory() {//создаем папку для фото
        directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.folder_name))
                .apply { if (!exists()) mkdirs() }
    }

    fun updateScreen() {
        fragment.updateNoteList(if (isFavOnScreen) getListOfFavoriteNotes() else getListOfAllNotes())
    }

    private fun isPermissionGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    private fun requestStoragePermissions() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, requestCodePerms)
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!isPermissionGranted()) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(this, getString(R.string.rationale), Toast.LENGTH_LONG).show()
                    Handler().postDelayed({ requestStoragePermissions() }, 1700L)
                } else {
                    requestStoragePermissions()
                    // No explanation needed, we can request the permission.
                    Snackbar.make(main_content, getString(R.string.manually), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_settings)) { startSettingsActivity() }
                            .show()
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }
        } else {
            // Permission doesn't need on <22
        }
    }

    private fun initPreferences() {
        spref = getPreferences(MODE_PRIVATE)

        if (spref.contains(getString(R.string.key_id))) {
            idToChangePhoto = spref.getInt(getString(R.string.key_id), -1)
        }
        if (spref.contains(getString(R.string.uri_key))) {
            uri = Uri.parse(spref.getString(getString(R.string.uri_key), null))
        }
        if (spref.contains(getString(R.string.key_code))) {
            code = spref.getInt(getString(R.string.key_code), -1)
        }
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                val list = if (!isFavOnScreen) getListOfAllNotes() else getListOfFavoriteNotes()
                val resultList = ArrayList<Note>()
                for (item in list) {
                    if (item.title?.contains(newText.toString(), true) == true || item.body?.contains(newText.toString(), true) == true) {
                        resultList.add(item)
                    }
                }
                fragment.updateNoteList(resultList)
                return true
            }
        })
    }

}
