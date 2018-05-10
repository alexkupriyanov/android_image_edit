package com.example.lbg99.andrapp

import android.Manifest
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_nav_menu.*
import kotlinx.android.synthetic.main.app_bar_nav_menu.*
import android.content.DialogInterface
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BitmapStorage {
    companion object {
        private var imageBitmap: Bitmap?=null
        private var currentPhotoPath: String?=null
    }
    fun saveChange() {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/capture_photo")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val OutletFname = "Image-$timeStamp.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            currentPhotoPath = file.absolutePath
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun setBitmap(newBitmap:Bitmap?){
        imageBitmap = newBitmap
    }
    fun getBitmap(): Bitmap? {
        return imageBitmap
    }
    fun getPath(): String? {
        return currentPhotoPath
    }
    fun setPath(newPath: String?) {
        currentPhotoPath = newPath
    }
    fun init(newBitmap: Bitmap, newPath: String) {
        setPath(newPath)
        setBitmap(newBitmap)
    }
}
class nav_menu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 2
    var mCurrentPhotoPath: String? = null
    var imageBitmap: Bitmap?=null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_menu)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            val content = arrayOf(getString(R.string.get_photo), getString(R.string.get_image))
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.take_quest)
                    .setItems(content, DialogInterface.OnClickListener { dialog, which ->
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0) {
                           takeAndSetPhoto()
                        }
                        if (which == 1) {
                            photoFromGallery()
                        }
                    })
            builder.show()
        }
        imageBitmap = (photoImageView.drawable as BitmapDrawable).bitmap
        saveBtn.setOnClickListener {
            saveImage()
            Toast.makeText(this, "Save complete!", Toast.LENGTH_SHORT).show()
        }
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun photoFromGallery() {
        val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        callGalleryIntent.type = "image/*"
        if (callGalleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(callGalleryIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun saveImage() {
        if (ActivityCompat.checkSelfPermission(applicationContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@nav_menu,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 102)
        }
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/capture_photo")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val OutletFname = "Image-$timeStamp.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap = (photoImageView.drawable as BitmapDrawable).bitmap
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            mCurrentPhotoPath = file.absolutePath
            out.close()
            BitmapStorage().setPath(mCurrentPhotoPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun takeAndSetPhoto(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val auth: String = packageName + ".fileprovider"
                val photoURI = FileProvider.getUriForFile(this,
                        auth,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedImage)
            photoImageView.setImageBitmap(imageBitmap)

        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            photoImageView.setImageBitmap(imageBitmap)
        }
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }
    fun randomMe () {
        val randomIntent = Intent(this, filter::class.java)
        randomIntent.putExtra(filter.absolutePath,mCurrentPhotoPath)
        startActivity(randomIntent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings ->{

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.smth -> {
                saveImage()
                randomMe()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}