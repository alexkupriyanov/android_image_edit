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

import android.view.View.Y




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
           for (i in 0 until matrix!!.size) {
               val color = matrix[i]
               val r = 255-Color.red(color)
               val g = 255-Color.green(color)
               val b = 255-Color.blue(color)
               val a=255
               val p = a shl 24 or (r shl 16) or (g shl 8) or b
               matrix[i] = p
           }
           var tmp: Bitmap? = Bitmap.createBitmap(getTmpImage()!!.width, getTmpImage()!!.height, Bitmap.Config.RGB_565)
           tmp!!.setPixels(matrix, 0, getTmpImage()!!.width, 0, 0, getTmpImage()!!.width, getTmpImage()!!.height)
           photoImageView.setImageBitmap(tmp)

        }
        sepiabutton.setOnClickListener {
            var r: Int
            var g: Int
            var b: Int
            var Y: Double
            var I: Double
            var Q: Double

            var matrix = getPixels()

            for (i in 0 until matrix!!.size)
            {
                val color = matrix[i]
                 r = Color.red(color)
                 g =  Color.green(color)
                 b =Color.blue(color)
                Y = 0.299 * r + 0.587 * g + 0.114 * b
                //I = (0.596 * r) - (0.274 * g) - (0.322 * b);
                //Q = (0.212 * r) - (0.523 * g) + (0.311 * b);

                //Update it
                I = 51.0
                Q = 0.0

                //Transform to RGB
                r = (1.0 * Y + 0.956 * I + 0.621 * Q).toInt()
                g = (1.0 * Y - 0.272 * I - 0.647 * Q).toInt()
                b = (1.0 * Y - 1.105 * I + 1.702 * Q).toInt()

                //Fix values
                r = if (r < 0) 0 else r
                r = if (r > 255) 255 else r

                g = if (g < 0) 0 else g
                g = if (g > 255) 255 else g

                b = if (b < 0) 0 else b
                b = if (b > 255) 255 else b
                val a=255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i] = p
            }

            var tmp: Bitmap? = Bitmap.createBitmap(getTmpImage()!!.width, getTmpImage()!!.height, Bitmap.Config.RGB_565)
            tmp!!.setPixels(matrix, 0, getTmpImage()!!.width, 0, 0, getTmpImage()!!.width, getTmpImage()!!.height)
            photoImageView.setImageBitmap(tmp)

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







