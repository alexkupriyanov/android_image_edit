package com.example.lbg99.andrapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.bitmap
import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import java.io.IOException
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import com.example.lbg99.andrapp.R.id.*


class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraBtn.setOnClickListener {
            val callCamerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(callCamerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCamerIntent,CAMERA_REQUEST_CODE)
            }
        }
        galeryBtn.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            callGalleryIntent.type = "image/*"
            if(callGalleryIntent.resolveActivity(packageManager)!=null) {
                startActivityForResult(callGalleryIntent,REQUEST_SELECT_IMAGE_IN_ALBUM)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap: Bitmap? = null
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    photoImageView.setImageBitmap(data.extras.get("data") as Bitmap)
                }
            }
            REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.getData()
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    photoImageView.setImageBitmap(bitmap)
                }
            }
            else ->  {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun countMe (view: View) {
        val randomIntent = Intent(this, Bin::class.java)
        startActivity(randomIntent)
    }

}
