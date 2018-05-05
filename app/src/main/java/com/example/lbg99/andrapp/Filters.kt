package com.example.lbg99.andrapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_filters.*
import kotlinx.android.synthetic.main.activity_filters.view.*
import java.io.IOException



class Filters :AppCompatActivity() {

    private val BIN_REQUEST_CODE = 0
    private val INVERSE_REQUEST_CODE = 1
    private val GRAY_REQUEST_CODE = 2
    private val SEPIA_REQUEST_CODE = 3

    companion object {
        var absolutePath: String? = null
    }

    var curPath: String? = null
    private var tmpImage: Bitmap? = null
    private var pixels: Array<IntArray>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)
        curPath = intent.getStringExtra(absolutePath)
        tmpImage = BitmapFactory.decodeFile(curPath)
        Image.setImageBitmap(tmpImage)
        getPixelsMatrix()
        binBtn.setOnClickListener {
            // пророговый фильтр(бинаризация)
            var matrix = pixels
            for (i in 0 until tmpImage!!.width)
                for(j in 0 until tmpImage!!.height)
                {
                    val color = matrix!![i][j]
                    val r = Color.red(color)
                    val g = Color.green(color)
                    val b = Color.blue(color)
                    val luminance = 0.299 * r + 0.0 + 0.587 * g + 0.0 + 0.114 * b + 0.0
                    matrix[i][j] = if (luminance > 125) Color.WHITE else Color.BLACK
                }
            var tmp: Bitmap? = Bitmap.createBitmap(tmpImage!!.width, tmpImage!!.height, Bitmap.Config.RGB_565)
            for(i in 0 until matrix!!.size)
                for(j in 0 until matrix[i].size)
                    tmp!!.setPixel(i,j,matrix[i][j])
            Image.setImageBitmap(tmp)
        }
        inversBtn.setOnClickListener {
            var matrix = pixels
            for (i in 0 until tmpImage!!.width)
                for(j in 0 until tmpImage!!.height)
                {
                    val color = matrix!![i][j]
                val r = 255 - Color.red(color)
                val g = 255 - Color.green(color)
                val b = 255 - Color.blue(color)
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i][j] = p
            }
            var tmp: Bitmap? = Bitmap.createBitmap(tmpImage!!.width, tmpImage!!.height, Bitmap.Config.RGB_565)
            for(i in 0 until matrix!!.size)
                for(j in 0 until matrix[i].size)
                    tmp!!.setPixel(i,j,matrix[i][j])
            Image.setImageBitmap(tmp)

        }
        grayscalebutton.setOnClickListener {

            var r: Int
            var g: Int
            var b: Int
            var Y: Double
            var I: Double
            var Q: Double
             var matrix = pixels
            for (i in 0 until tmpImage!!.width)
                for(j in 0 until tmpImage!!.height)
                {
                    val color = matrix!![i][j]
                r = Color.red(color)
                g = Color.green(color)
                b = Color.blue(color)
                Y = 0.299 * r + 0.587 * g + 0.114 * b
                I = 1.0
                Q = 0.0
                //Transform to RGB
                r = (1.0 * Y + 0.999 * I + 0.621 * Q).toInt()
                g = (1.0 * Y - 0.272 * I - 0.647 * Q).toInt()
                b = (1.0 * Y - 1.105 * I + 1.702 * Q).toInt()
                //Fix values
                r = if (r < 0) 0 else r
                r = if (r > 255) 255 else r
                g = if (g < 0) 0 else g
                g = if (g > 255) 255 else g
                b = if (b < 0) 0 else b
                b = if (b > 255) 255 else b
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i][j] = p
            }
            var tmp: Bitmap? = Bitmap.createBitmap(tmpImage!!.width, tmpImage!!.height, Bitmap.Config.RGB_565)
            for(i in 0 until matrix!!.size)
                for(j in 0 until matrix[i].size)
                    tmp!!.setPixel(i,j,matrix[i][j])
            Image.setImageBitmap(tmp)

        }
        sepiabutton.setOnClickListener {

            var r: Int
            var g: Int
            var b: Int
            var Y: Double
            var I: Double
            var Q: Double
             var matrix = pixels
            for (i in 0 until tmpImage!!.width)
                for(j in 0 until tmpImage!!.height)
                {
                    val color = matrix!![i][j]
                r = Color.red(color)
                g = Color.green(color)
                b = Color.blue(color)
                Y = 0.299 * r + 0.587 * g + 0.114 * b
                I = 51.0
                Q = 0.0
                //Transform to RGB
                r = (1.0 * Y + 0.999 * I + 0.621 * Q).toInt()
                g = (1.0 * Y - 0.272 * I - 0.647 * Q).toInt()
                b = (1.0 * Y - 1.105 * I + 1.702 * Q).toInt()
                //Fix values
                r = if (r < 0) 0 else r
                r = if (r > 255) 255 else r
                g = if (g < 0) 0 else g
                g = if (g > 255) 255 else g
                b = if (b < 0) 0 else b
                b = if (b > 255) 255 else b
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i][j] = p
            }
           var tmp: Bitmap? = Bitmap.createBitmap(tmpImage!!.width, tmpImage!!.height, Bitmap.Config.RGB_565)
            for(i in 0 until matrix!!.size)
                for(j in 0 until matrix[i].size)
                    tmp!!.setPixel(i,j,matrix[i][j])
            Image.setImageBitmap(tmp)

        }
        gaussbutton.setOnClickListener{
            gauss()

        }
    }

    fun getPixelsMatrix() { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr:Array<IntArray>? = Array(tmpImage!!.width, { IntArray(tmpImage!!.height) })
        for(i in 0 until tmpImage!!.width)
            for(j in 0 until tmpImage!!.height)
                arr!![i][j]= tmpImage!!.getPixel(i,j)
        pixels = arr // закинули в глобальный массив
    }
    fun gauss(){
        var r=1
        var matrix = pixels
        var matrix1 = pixels
        val rs = Math.ceil(r * 2.57)     // significant radius
        for (i in 0 until tmpImage!!.height)
            for (j in 0 until tmpImage!!.width) {
                var valu = 0
                var wsum = 0
                 var t=i-rs.toInt()
                 var s=j-rs.toInt()
                for (iy in t until i+rs.toInt()+1)
                {
                    for (ix in s until j+rs.toInt()+1)
                    {
                        val x = Math.min(tmpImage!!.width - 1, Math.max(0, ix))
                        val y = Math.min(tmpImage!!.height - 1, Math.max(0, iy))
                        val dsq = (ix - j) * (ix - j) + (iy - i) * (iy - i)
                        if((Math.PI * 2 * r * r).toInt() != 0)
                        {
                        val wght = Math.exp((-dsq / (2 * r * r)).toDouble()) / (Math.PI * 2 * r * r)

                        valu += matrix!![y ][ x] * wght.toInt()
                        wsum += wght.toInt()
                        }
                        else{
                            val wght = Math.exp((-dsq / (2 * r * r)).toDouble())

                            valu += matrix!![y ][ x] * wght.toInt()
                            wsum += wght.toInt()

                        }
                    }
                }
                matrix1!![i][j] = Math.round((valu / wsum).toFloat())
            }
        var tmp: Bitmap? = Bitmap.createBitmap(tmpImage!!.width, tmpImage!!.height, Bitmap.Config.RGB_565)
        for(i in 0 until matrix1!!.size)
            for(j in 0 until matrix1[i].size)
                tmp!!.setPixel(i,j,matrix1[i][j])
        Image.setImageBitmap(tmp)
    }

}