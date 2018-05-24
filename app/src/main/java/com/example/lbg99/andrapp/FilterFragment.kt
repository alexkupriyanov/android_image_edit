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
import android.widget.Spinner
import android.widget.Toast
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
                }
            }
        }
    }

    fun getPixelsMatrix(tmpImage: Bitmap?): IntArray { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr = IntArray(tmpImage!!.width * tmpImage!!.height)
        /*for(i in 0 until tmpImage!!.width)
            for(j in 0 until tmpImage!!.height) {
                arr[i][j]= tmpImage!!.getPixel(i, j)
            }*/
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