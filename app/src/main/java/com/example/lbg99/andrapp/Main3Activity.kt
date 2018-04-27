package com.example.lbg99.andrapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {

    val cam_code = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        camera.setOnClickListener {
            val call = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(call.resolveActivity(packageManager)!=null) {
                startActivityForResult(call,cam_code)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            cam_code -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    imageView2.setImageBitmap(data.extras.get("data") as Bitmap)
                }
            }
            else -> {
                Toast.makeText(this,"ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
