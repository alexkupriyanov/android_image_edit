package com.example.lbg99.andrapp

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_unsharp_masking.*
import android.widget.SeekBar
import android.graphics.Bitmap
import android.graphics.Color
import com.example.lbg99.andrapp.R.id.*


class UnsharpMaskingFragment : Fragment() {

    var tmpImage : Bitmap? = null
    val PI = 3.141592653
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        tmpImage = commonData.imageBitmap
        maskingView.setImageBitmap(commonData.imageBitmap)

        cancelMaskBtn.setOnClickListener {
            maskingView.setImageBitmap(commonData.imageBitmap)
            tmpImage = commonData.imageBitmap
            amountPicker.value = amountPicker.minValue
            trashPicker.value = trashPicker.minValue
            radiusPicker.value = radiusPicker.minValue
        }

        amountPicker.minValue = 0
        amountPicker.maxValue = 50
        trashPicker.minValue = 0
        trashPicker.maxValue = 255
        radiusPicker.minValue = 1
        radiusPicker.maxValue = 10

        applyMaskBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }

        doBtn.setOnClickListener {
            tmpImage = applyGaussianBlur(commonData.imageBitmap!!, radiusPicker.value, trashPicker.value.toDouble(), amountPicker.value.toDouble())
            maskingView.setImageBitmap(tmpImage)
        }
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
        var sumG: Double  // переменные для вычисления суммы цвета
        var sumB: Double
        var x1 = 0
        var y1 = 0
        val result = Bitmap.createBitmap(width, height, src.config)

        for (x in -radius until radius) {
            for (y in -radius until radius) {
                weights[x1][y1] = (Math.pow (Math.E, (-((x * x + y * y) / (2 * radius * radius))).toDouble())) / (2 * PI * radius * radius)
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
                sumG = 0.0
                sumR = 0.0

                for (i in 0 until SIZE) {
                    for (j in 0 until SIZE) {
                        sumR += ((Color.red(pixels[x+i+width*(y+j)]) * weights[i][j]))
                        sumG += ((Color.green(pixels[x+i+width*(y+j)]) * weights[i][j]))  // считаем сумму для цветов
                        sumB += ((Color.blue(pixels[x+i+width*(y+j)]) * weights[i][j]))
                    }
                }

                // получаем итоговые цвета
                var R = Math.max(0, Math.min(255, (sumR / sum).toInt()))
                var G = Math.max(0, Math.min(255, (sumG / sum).toInt()))
                var B = Math.max(0, Math.min(255, (sumB / sum).toInt()))

                var diff = (R - Color.red(pixels[(x+1)+(y+1)*width]) + G - Color.green(pixels[(x+1)+(y+1)*width]) + B - Color.blue(pixels[(x+1)+(y+1)*width])) / 3

                if (Math.abs(2 * diff) > threshold) {
                    R = Color.red(pixels[(x + 1) + (y + 1) * width]) + (diff * amount).toInt()
                    G = Color.green(pixels[(x + 1) + (y + 1) * width]) + (diff * amount).toInt()
                    B = Color.blue(pixels[(x + 1) + (y + 1) * width]) + (diff * amount).toInt()
                    R = Math.max(0, Math.min(255, R))
                    G = Math.max(0, Math.min(255, G))
                    B = Math.max(0, Math.min(255, B))
                }
                result.setPixel(x + 1, y + 1, Color.argb(255, R, G, B))
            }
        }
        return result
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_unsharp_masking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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