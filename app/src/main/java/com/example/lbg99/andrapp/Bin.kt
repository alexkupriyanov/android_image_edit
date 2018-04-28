package com.example.lbg99.andrapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Color
import android.content.Context

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Bin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bin)
    }

    fun binarizeByThreshold(imagePath: String, threshold: Int) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height
        val pixels = IntArray(size)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        for (i in 0 until size) {
            val color = pixels[i]
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val luminance = 0.299 * r + 0.0 + 0.587 * g + 0.0 + 0.114 * b + 0.0
            pixels[i] = if (luminance > threshold) Color.WHITE else Color.BLACK
        }

        Utils.saveBitmap(imagePath, width, height, pixels)
    }
}
