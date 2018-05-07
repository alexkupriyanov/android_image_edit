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
import com.example.lbg99.andrapp.Filters.ConvolutionMatrix.Companion.computeConvolution
import kotlinx.android.synthetic.main.activity_filters.*
import kotlinx.android.synthetic.main.activity_filters.view.*
import java.io.IOException
import kotlin.math.*


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
                applyGaussianBlur(tmpImage!!)

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


    fun applyGaussianBlur(src: Bitmap) {
//set gaussian blur configuration
        val Radius = 3
// create instance of Convolution matrix
        var weigts = Array(Radius, { DoubleArray(Radius) })
        var half = floor((Radius/2).toDouble())
        var sum=0.0

        for (x in 0 until Radius.toInt()) {
            for (y in 0 until Radius.toInt()) {
                var x1=x-half
                var y1 = y-half

                var value1=(1 / (2 * PI * half * half)) * exp(-(x1 * x1 + y1 * y1) / (2 * half * half))

                weigts[x][y]=value1
                sum+=value1

            }
        }

        for (i in 0 until weigts.size) {
            for (j in 0 until weigts[i].size) {
                weigts[i][j] /= sum
            }
        }


        val convMatrix = ConvolutionMatrix(weigts.size)
// Apply Configuration
        convMatrix.applyConfig(weigts)
        val width = src.getWidth()
        val height = src.getHeight()
//return out put bitmap
        var t:Bitmap? =computeConvolution(src, convMatrix)
        Image.setImageBitmap(t)

    }


    class ConvolutionMatrix(size: Int) {
        var Matrix: Array<DoubleArray>
        var Factor = 1.0
        var Offset = 1.0

        init {
            Matrix = Array(size, { DoubleArray(size) })
        }

        fun applyConfig(config: Array<DoubleArray>) {
            for (x in 0 until SIZE) {
                for (y in 0 until SIZE) {
                    Matrix[x][y] = config[x][y]
                }
            }
        }

        companion object {
            val Radius = 1
            val SIZE = Radius*2+1


            fun computeConvolution(src: Bitmap, matrix: ConvolutionMatrix): Bitmap {
                val width = src.getWidth()
                val height = src.getHeight()
                val result = Bitmap.createBitmap(width, height, src.getConfig())
                var A: Int
                var R: Int
                var G: Int
                var B: Int
                var sumR: Double
                var sumG: Double
                var sumB: Double
                var pixels1 = Array<IntArray>(width, { IntArray(height)})
                var side= round(sqrt(SIZE.toDouble()))
                var side1= floor(side/2)

                for (x in 0 until width ) {
                    for (y in 0 until height) {
// get pixel matrix
                        // get alpha of center pixel
                        A = Color.alpha(pixels1[1][1])
// init color sum
                        sumB = 0.0
                        sumG = 0.0
                        sumR = 0.0
                        // get pixel matrix
                        var i =0
                        var j =0
                        // get pixel matrix

                                pixels1[x][y] = src.getPixel(x, y)


                        for (i in 0 until side.toInt())
                        {
                            for (j in 0 until side.toInt())
                            {
                                var yr= (y + i - side1).toInt()
                                var xr=( x + i - side1).toInt()

                                if(yr>=0 && xr>=0 && yr < height && xr < width){
                                    sumR += (Color.red(pixels1[yr][xr]) * matrix.Matrix[i][j])
                                    sumG += (Color.green(pixels1[yr][xr]) * matrix.Matrix[i][j])
                                    sumB += (Color.blue(pixels1[yr][xr]) * matrix.Matrix[i][j])

                                }// get sum of RGB on matrix

                            }
                        }
// get final Red
                            //R = (sumR / matrix.Factor + matrix.Offset).toInt()
                        R=sumR.toInt()
                            if (R < 0) {
                                R = 0
                            } else if (R > 255) {
                                R = 255
                            }
// get final Green
                           // G = (sumG / matrix.Factor + matrix.Offset).toInt()
                        G=sumG.toInt()
                            if (G < 0) {
                                G = 0
                            } else if (G > 255) {
                                G = 255
                            }
// get final Blue
                            //B = (sumB / matrix.Factor + matrix.Offset).toInt()
                        B=sumB.toInt()
                            if (B < 0) {
                                B = 0
                            } else if (B > 255) {
                                B = 255
                            }
// apply new pixel
                            result.setPixel(x, y, Color.argb(A, R, G, B))
                        }
                    }
                return result
            }
        }
    }
}