package com.example.lbg99.andrapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.lbg99.andrapp.R.id.photoImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 2
    var mCurrentPhotoPath: String? = null
    public var imageBitmap: Bitmap?=null
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
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraBtn.setOnClickListener {
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
        galeryBtn.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            callGalleryIntent.type = "image/*"
            if (callGalleryIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callGalleryIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
        filtBtn.setOnClickListener {

            randomMe()
        }
        imageBitmap = (photoImageView.drawable as BitmapDrawable).bitmap
        saveBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity,
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
                    out.close()
                    Toast.makeText(this, "Save complete!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        }
    }
     fun randomMe () {
         val randomIntent = Intent(this, Filters::class.java)
         startActivity(randomIntent)
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
}