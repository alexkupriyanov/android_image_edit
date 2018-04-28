package com.example.lbg99.andrapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
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

        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    photoImageView.setImageBitmap(data.extras.get("data") as Bitmap)
                }
            }
            REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    photoImageView.setImageBitmap(data.extras.get("data") as Bitmap)
                }
            }
            else ->  {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
