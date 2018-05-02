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
import android.R.attr.height
import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.util.Log
import android.graphics.drawable.BitmapDrawable
import java.nio.ByteBuffer
import java.util.Collections.rotate

import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth

import android.view.View.Y

import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build.VERSION_CODES.M

import android.view.View.Y
import com.example.lbg99.andrapp.R.id.*


class MainActivity : AppCompatActivity() {
    private var tpmImage: Bitmap? = null

    fun setTmpImage(value: Bitmap?) { // заполняем TmpImage
        tpmImage = value
    }

    fun getTmpImage(): Bitmap? { // получает значения TmpImage для работы с ними
        return tpmImage
    }

    private var pixels: IntArray? = null

    fun setPixels(value: IntArray?) {  // заполняем  pixels
        pixels = value
    }

    fun getPixels(): IntArray? { // получает значения pixels для работы с ними
        return pixels
    }

    private val CAMERA_REQUEST_CODE = 0
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraBtn.setOnClickListener {
            val callCamerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (callCamerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callCamerIntent, CAMERA_REQUEST_CODE)
            }
        }
        galeryBtn.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            callGalleryIntent.type = "image/*"
            if (callGalleryIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(callGalleryIntent, REQUEST_SELECT_IMAGE_IN_ALBUM)
            }
        }
        binBtn.setOnClickListener {
            // пророговый фильтр(бинаризация)
            var matrix = getPixels()
            for (i in 0 until matrix!!.size) {
                val color = matrix[i]
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                val luminance = 0.299 * r + 0.0 + 0.587 * g + 0.0 + 0.114 * b + 0.0
                matrix[i] = if (luminance > 125) Color.WHITE else Color.BLACK
            }
            var tmp: Bitmap? = Bitmap.createBitmap(getTmpImage()!!.width, getTmpImage()!!.height, Bitmap.Config.RGB_565)
            tmp!!.setPixels(matrix, 0, getTmpImage()!!.width, 0, 0, getTmpImage()!!.width, getTmpImage()!!.height)
            photoImageView.setImageBitmap(tmp)

        }
       sepBtn.setOnClickListener {
        var matrix = getPixels()
           var bmp: Bitmap? = null
           for (i in 0 until matrix!!.size) {
           var p = matrix[i]
               val a = p shr 24 and 0xff
               var r = p shr 16 and 0xff
               var g = p shr 8 and 0xff
               var b = p and 0xff

               //calculate tr, tg, tb
               val tr = (0.393 * r + 0.769 * g + 0.189 * b).toInt()
               val tg = (0.349 * r + 0.686 * g + 0.168 * b).toInt()
               val tb = (0.272 * r + 0.534 * g + 0.131 * b).toInt()


                //Fix values
               if(tr > 255){
                   r = 255;
               }else{
                   r = tr;
               }

               if(tg > 255){
                   g = 255;
               }else{
                   g = tg;
               }

               if(tb > 255){
                   b = 255;
               }else{
                   b = tb;
               }
               var tmp: Bitmap?  = Bitmap.createBitmap(getTmpImage()!!.width, getTmpImage()!!.height, Bitmap.Config.RGB_565)
               tmp!!.setPixels(matrix, 0, getTmpImage()!!.width, 0, 0, getTmpImage()!!.width, getTmpImage()!!.height)
bmp=tmp
        }

        photoImageView.setImageBitmap(bmp)

    }
    }



        fun getPixelsMatrix() { //получает матрицу пикселей из bitmap (просто интовые байты)
            var tempImg = getTmpImage()
            //var pixels = Array<int>(tempImg!!.width*tempImg!!.height)

            var arr = IntArray(getTmpImage()!!.width * getTmpImage()!!.height)

            getTmpImage()!!.getPixels(arr, 0, getTmpImage()!!.width, 0, 0, getTmpImage()!!.width, getTmpImage()!!.height) //получаем матрицу пикселей и записывает в массив
            setPixels(arr) // закинули в глобальный массив
        }

        fun normalizeBmp(value: Bitmap?): Bitmap? {
            var max = 3000
            var outWidth = 0
            var outHeight = 0
            var widthI = value!!.width
            var heightI = value!!.height
            if (widthI > heightI) {
                outWidth = max
                outHeight = (heightI * max) / widthI
            } else {
                outHeight = max
                outWidth = (widthI * max) / heightI
            }
            var tmp: Bitmap? = null
            tmp = Bitmap.createScaledBitmap(value, outWidth, outHeight, false)
            value.recycle()
            return tmp
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            var bitmap: Bitmap? = null
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        var bmp: Bitmap? = null
                        bmp = normalizeBmp(data.extras.get("data") as Bitmap)
                        photoImageView.setImageBitmap(bmp)
                        setTmpImage(bmp)
                        getPixelsMatrix()
                    }
                }
                REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                    if (resultCode === Activity.RESULT_OK) {
                        val selectedImage = data?.getData()
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                            bitmap = normalizeBmp(bitmap)
                            setTmpImage(bitmap)
                            getPixelsMatrix()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        photoImageView.setImageBitmap(bitmap)
                    }
                }
                else -> {
                    Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }







