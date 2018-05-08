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
import android.widget.ImageView
import android.widget.Toast
import com.example.lbg99.andrapp.R.id.*
//import com.example.lbg99.andrapp.Filters.ConvolutionMatrix.Companion.computeConvolution
import kotlinx.android.synthetic.main.activity_filters.*
import kotlinx.android.synthetic.main.activity_filters.view.*
import java.io.IOException
import kotlin.math.*
import kotlin.text.Typography.half


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
                Image.setImageBitmap(applyGaussianBlur(tmpImage!!, 3,0.0,2.0))

               }

}

    fun getPixelsMatrix()
    { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr:Array<IntArray>? = Array(tmpImage!!.width, { IntArray(tmpImage!!.height) })
        for(i in 0 until tmpImage!!.width)
            for(j in 0 until tmpImage!!.height)
                arr!![i][j]= tmpImage!!.getPixel(i,j)
        pixels = arr // закинули в глобальный массив
    }


    fun  applyGaussianBlur(src: Bitmap, radius: Int,threshold:Double,amount:Double): Bitmap  {

        val SIZE = 2 * radius + 1
        var sum = 0.0
        var pixels = IntArray(src!!.width * src!!.height)
        val weights = Array(SIZE) { DoubleArray(SIZE) } // матрица коэффициентов(весов)
        val width = src.width
        val height = src.height
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        var sumR: Double
        var sumG:Double  // переменные для вычисления суммы цвета
        var sumB: Double

        var x1 = 0
        var y1 = 0
        val result = Bitmap.createBitmap(width, height, src.config)

        for (x in -radius until radius) {
            for (y in -radius until radius) {
                weights[x1][y1] = (Math.pow (Math.E, (-((x*x+y*y)/(2*radius*radius))).toDouble())) / (2*PI*radius*radius)
               // weigts[x1][y1] =(1 / (2 * PI * radius * radius)) * exp(-(x1 * x1 + y1 * y1) / (2 * radius * radius).toDouble())
                sum += weights[x1][y1]
                y1++
            }
            y1 = 0
            x1++
        }

        if (sum == 0.0)  // чтобы избежать деления на 0
            sum = 1.0

        for (y in 0 until height - SIZE + 1) {
            for (x in 0 until width - SIZE + 1) {


                sumB = 0.0
                sumG = sumB
                sumR = sumG

                for (i in 0 until SIZE) {
                    for (j in 0 until SIZE) {
                        sumR += ((Color.red(pixels[x+i+width*(y+j)]) * weights[i][j]))
                        sumG += ((Color.green(pixels[x+i+width*(y+j)]) * weights[i][j]))  // считаем сумму для цветов
                        sumB += ((Color.blue(pixels[x+i+width*(y+j)]) * weights[i][j]))
                    }
                }

               var R = (sumR/sum).toInt()
                if (R < 0) {
                    R = 0
                } else if (R > 255) {
                    R = 255
                }
                                                // получаем итоговые цвета
               var G = (sumG/sum).toInt()
                if (G < 0) {
                    G = 0
                } else if (G > 255) {
                    G = 255
                }

              var  B = (sumB/sum).toInt()
                if (B < 0) {
                    B = 0
                } else if (B > 255) {
                    B = 255
                }

                var diff = (R - Color.red(pixels[(x+1)+(y+1)*width]) + G - Color.green(pixels[(x+1)+(y+1)*width]) + B - Color.blue(pixels[(x+1)+(y+1)*width])) / 3;
                if (Math.abs(2*diff) > threshold) {
                    R = Color.red(pixels[(x+1)+(y+1)*width]) + (diff*amount).toInt()
                    G = Color.green(pixels[(x+1)+(y+1)*width]) + (diff*amount).toInt()
                    B = Color.blue(pixels[(x+1)+(y+1)*width]) + (diff*amount).toInt()
                    if (R > 255) R = 255
                    if (R < 0) R = 0
                    if (G > 255) G = 255
                    if (G < 0) G = 0
                    if (B > 255) B = 255
                    if (B < 0) B = 0
                }
                result.setPixel(x + 1, y + 1, Color.argb(255, R, G, B));
            }
            }

        //imageview.setImageBitmap(result)
        return result
    }

}