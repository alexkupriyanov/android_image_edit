package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : Fragment() {

    val TAG = "fragmentFilters"
    var curPath: String? = null
    private var tmpImage: Bitmap? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        curPath = commonData.currentPhotoPath
        tmpImage = commonData.imageBitmap
        var width = tmpImage!!.width
        var height = tmpImage!!.height
        photoView.setImageBitmap(tmpImage)
        var oldPixels: IntArray = getPixelsMatrix(tmpImage)
        var pixels: IntArray = oldPixels
        cancelFilterBtn.setOnClickListener {
            pixels = oldPixels
            tmpImage = commonData.imageBitmap
            photoView.setImageBitmap(tmpImage)
        }

        applyFilterBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && pixels == null)
                    pixels = getPixelsMatrix(tmpImage)
                when (position) {

                    1 -> {
                        pixels = bin(pixels)
                        tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                        photoView.setImageBitmap(tmpImage)
                    }

                    2 -> {
                        pixels = inverse(pixels)
                        tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                        photoView.setImageBitmap(tmpImage)
                    }

                    3 -> {
                        pixels= gray(pixels)
                        tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                        photoView.setImageBitmap(tmpImage)
                    }

                    4 -> {
                        pixels= sepia(pixels)
                        tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                        photoView.setImageBitmap(tmpImage)
                    }
                   5-> {
                       pixels= fog(pixels,1.8,1.8,1.8)
                       tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                       photoView.setImageBitmap(tmpImage)
                    }
                    6-> {
                        pixels= blur(pixels)
                        tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                        photoView.setImageBitmap(tmpImage)
                    }
                }
            }
        }
    }

    fun getPixelsMatrix(tmpImage: Bitmap?): IntArray { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr = IntArray(tmpImage!!.width * tmpImage!!.height)
        tmpImage!!.getPixels(arr, 0, tmpImage!!.width, 0, 0, tmpImage!!.width, tmpImage!!.height)
        return arr // закинули в глобальный массив
    }

    fun bin(pxl : IntArray) : IntArray {
        var matrix = pxl
        for (i in 0 until tmpImage!!.height)
            for(j in 0 until tmpImage!!.width)
            {
                val color = matrix!![i * tmpImage!!.width + j]
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                val luminance = 0.299 * r + 0.587 * g + 0.114 * b
                matrix[i * tmpImage!!.width + j] = if (luminance > 125) Color.WHITE else Color.BLACK
            }
        return matrix
    }

    fun inverse(pxl : IntArray) : IntArray {
        var matrix = pxl
        for (i in 0 until tmpImage!!.height)
            for(j in 0 until tmpImage!!.width)
            {
                val color = matrix!![i * tmpImage!!.width + j]
                val r = 255 - Color.red(color)
                val g = 255 - Color.green(color)
                val b = 255 - Color.blue(color)
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i * tmpImage!!.width + j] = p
            }
        return matrix
    }

    fun gray(pxl : IntArray) : IntArray {
        var r: Int
        var g: Int
        var b: Int
        var Y: Double
        var I: Double
        var Q: Double
        var matrix = pxl
        for (i in 0 until tmpImage!!.height)
            for (j in 0 until tmpImage!!.width) {
                val color = matrix!![i * tmpImage!!.width + j]
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
                r = Math.max(0, Math.min(255, r))
                g = Math.max(0, Math.min(255, g))
                b = Math.max(0, Math.min(255, b))
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i * tmpImage!!.width + j] = p
            }
        return matrix
    }

    fun sepia(pxl : IntArray) : IntArray {
        var r: Int
        var g: Int
        var b: Int
        var Y: Double
        var I: Double
        var Q: Double
        var matrix = pxl
        for (i in 0 until tmpImage!!.height)
            for(j in 0 until tmpImage!!.width)
            {
                val color = matrix!![i * tmpImage!!.width + j]
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
                r = Math.max(0, Math.min(255, r))
                g = Math.max(0, Math.min(255, g))
                b = Math.max(0, Math.min(255, b))
                val a = 255
                val p = a shl 24 or (r shl 16) or (g shl 8) or b
                matrix[i * tmpImage!!.width + j] = p
            }
        return matrix
    }

    val COLOR_MIN = 0x00
    val COLOR_MAX = 0xFF

    fun fog(pxl : IntArray , red: Double, green: Double, blue: Double): IntArray{
        // create output image
        var matrix = pxl
        // get image size
        val width = tmpImage!!.width
        val height = tmpImage!!.height
        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int
        // constant value curve
        val MAX_SIZE = 256
        val MAX_VALUE_DBL = 255.0
        val MAX_VALUE_INT = 255
        val REVERSE = 1.0

        // gamma arrays
        val gammaR = IntArray(MAX_SIZE)
        val gammaG = IntArray(MAX_SIZE)
        val gammaB = IntArray(MAX_SIZE)

        // setting values for every gamma channels
        for (i in 0 until MAX_SIZE) {
            gammaR[i] = Math.min(MAX_VALUE_INT,
                    (MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red) + 0.5).toInt())
            gammaG[i] = Math.min(MAX_VALUE_INT,
                    (MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green) + 0.5).toInt())
            gammaB[i] = Math.min(MAX_VALUE_INT,
                    (MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue) + 0.5).toInt())
        }

        // apply gamma table
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                val color = matrix!![x * tmpImage!!.width + y]

                A = Color.alpha(color)
                // look up gamma
                R = gammaR[Color.red(color)]
                G = gammaG[Color.green(color)]
                B = gammaB[Color.blue(color)]
                // set new color to output bitmap
                val p = A shl 24 or (R shl 16) or (G shl 8) or B
                matrix[x * tmpImage!!.width + y] = p

            }
        }

        // return final image
        return matrix
    }

    fun blur(pxl: IntArray): IntArray {

        val radius = 5
        val SIZE = 2 * radius + 1
        var sum = 0.0
        var matrix = pxl
        val weights = Array(SIZE) { DoubleArray(SIZE) } // матрица коэффициентов(весов)
        val width = tmpImage!!.width
        val height = tmpImage!!.height

        var sumR: Double
        var sumG: Double  // переменные для вычисления суммы цвета
        var sumB: Double

        var x1 = 0
        var y1 = 0

        for (x in -radius until radius) {
            for (y in -radius until radius) {
                weights[x1][y1] = (Math.pow(Math.E, (-((x * x + y * y) / (2 * radius * radius))).toDouble())) / (2 * Math.PI * radius * radius)
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
                        sumR += ((Color.red(matrix[x + i + width * (y + j)]) * weights[i][j]))
                        sumG += ((Color.green(matrix[x + i + width * (y + j)]) * weights[i][j]))  // считаем сумму для цветов
                        sumB += ((Color.blue(matrix[x + i + width * (y + j)]) * weights[i][j]))
                    }
                }

                var R = (sumR / sum).toInt()
                if (R < 0) {
                    R = 0
                } else if (R > 255) {
                    R = 255
                }
                // получаем итоговые цвета
                var G = (sumG / sum).toInt()
                if (G < 0) {
                    G = 0
                } else if (G > 255) {
                    G = 255
                }

                var B = (sumB / sum).toInt()
                if (B < 0) {
                    B = 0
                } else if (B > 255) {
                    B = 255
                }
                val a = 255
                val p = a shl 24 or (R shl 16) or (G shl 8) or B
                matrix[y * tmpImage!!.width + x] = p

            }
        }
        return matrix
    }



    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
    }
}