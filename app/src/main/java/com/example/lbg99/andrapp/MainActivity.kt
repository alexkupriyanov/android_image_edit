package com.example.lbg99.andrapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.lbg99.andrapp.R.id.photoImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import java.net.URI


class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private var mImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraBtn.setOnClickListener {
            val callCamerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photo: File
            photo = this.createTemporaryFile("pic",".jpg")
            photo.delete()
            mImageUri = Uri.fromFile(photo);
            callCamerIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(callCamerIntent, CAMERA_REQUEST_CODE);
        }
        galeryBtn.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            callGalleryIntent.type = "image/*"
            if(callGalleryIntent.resolveActivity(packageManager)!=null) {
                startActivityForResult(callGalleryIntent,REQUEST_SELECT_IMAGE_IN_ALBUM)
            }
        }
    }

    private fun createTemporaryFile(part: String, ext: String): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.absolutePath + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap: Bitmap? = null
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)
                    photoImageView.setImageBitmap(bitmap)
                }
            }
            REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.getData()
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    photoImageView.setImageBitmap(bitmap)
                }
            }
            else ->  {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
