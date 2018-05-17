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
            alphaSeek.progress = 0
            trashSeek.progress = 0
            radiusSeek.progress = 0
            alphaValue.text = "0"
            trashValue.text = "0"
            radiusValue.text = "1"

        }

        applyMaskBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }

        doBtn.setOnClickListener {
            tmpImage = applyGaussianBlur(commonData.imageBitmap!!, radiusValue.text.toString().toInt() + 1, trashValue.text.toString().toDouble(), alphaValue.text.toString().toDouble())
            maskingView.setImageBitmap(tmpImage)
        }

        alphaSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alphaValue.text = (progress.toDouble()/10).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        trashSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                trashValue.text = progress.toInt().toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        radiusSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radiusValue.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
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
                result.setPixel(x + 1, y + 1, Color.argb(255, R, G, B))
            }
        }

        //imageview.setImageBitmap(result)
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