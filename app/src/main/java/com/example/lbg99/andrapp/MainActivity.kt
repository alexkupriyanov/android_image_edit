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
        whitebutton.setOnClickListener {
            var maxR = 1.0
            var maxG = 1.0
            var maxB = 1.0
            var matrix = getPixels()
            for (i in 0 until matrix!!.size) {
                val color = matrix[i]
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                if (r > maxR)
                    maxR = r.toDouble()

                if (g > maxG)
                    maxG = g.toDouble()

                if (b > maxB)
                    maxB = b.toDouble()


            }
            maxR = 255.0 / maxR
            maxG = 255.0 / maxG
            maxB = 255.0 / maxB
            for (i in 0 until matrix!!.size)
            {
                val color = matrix[i]
                var r = maxR*Color.red(color)
                var g =  maxG*Color.green(color)
                var b = maxB*Color.blue(color)
                if (r > 255) r = 255.0
                if (g > 255) g = 255.0
                if (b > 255) b = 255.0

                val a=255
                val p = a shl 24 or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
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







