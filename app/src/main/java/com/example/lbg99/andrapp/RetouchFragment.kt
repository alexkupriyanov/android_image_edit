package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_retouch.*
import kotlinx.android.synthetic.main.nav_header_nav_menu.*

class RetouchFragment : Fragment() {

    var tmpImage: Bitmap? = null
    var radius: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        retouchView.setImageBitmap(commonData.imageBitmap)
        tmpImage = commonData.imageBitmap

        radiusRetouchPicker.minValue = 1
        radiusRetouchPicker.maxValue = 50

        radiusRetouchPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
                radius = radiusRetouchPicker.value
            }
        })

        retouchView.setOnTouchListener(object:View.OnTouchListener {
            override  fun  onTouch(v:View, event: MotionEvent):Boolean {

                if (event.getAction() === MotionEvent.ACTION_MOVE){
                    var x = event.x
                    var y = event.y
                    if (x > retouchView.width / 2 + tmpImage!!.width / 2 || x < retouchView.width / 2 - tmpImage!!.width / 2
                    || y > retouchView.height / 2 + tmpImage!!.height / 2 || y < retouchView.height / 2 - tmpImage!!.height / 2) {
                        Toast.makeText(context,"Out of picture", Toast.LENGTH_SHORT).show()
                        return true
                    }
                    val m_size = 2 * radius + 1
                    val width = tmpImage!!.width
                    val height = tmpImage!!.height
                    var pixels = IntArray(width * height)
                    tmpImage!!.getPixels(pixels, 0, width, 0, 0, width, height)
                    var sumR = 0
                    var sumG = 0
                    var sumB = 0
                    var count = 0
                    val weights = Array<DoubleArray>(m_size, {DoubleArray(m_size)})
                    for (i in -radius until radius)// считаем коэффициент и количество пикселей в выбранном радиусе
                    {
                        for (j in -radius until radius)
                        {
                            val dX = i.toDouble()
                            val dY = j.toDouble()
                            val dR = radius.toDouble()
                            weights[i + radius][j + radius] = (Math.pow(Math.E, -((dX * dX + dY * dY) / (2.0 * dR * dR)))) * 0.25
                        }
                    }
                    val s1= Math.max(0, Math.min(width, (x - radius).toInt() - retouchView.width / 2 + width / 2))
                    val f1= Math.max(0, Math.min(width, (x + radius).toInt() - retouchView.width / 2 + width / 2))
                    val s2= Math.max(0, Math.min(height, (y - radius).toInt() - retouchView.height / 2 + height / 2))
                    val f2= Math.max(0, Math.min(height, (y + radius).toInt() - retouchView.height / 2 + height / 2))
                    for (i in s1 until f1) {
                        for (j in s2 until f2) {
                                sumR += Color.red(pixels[j * width + i])
                                sumG += Color.green(pixels[j * width + i])   // считаем сумму для цветов
                                sumB += Color.blue(pixels[j * width + i])
                                count++
                        }
                    }
                    sumR/=count
                    sumG/=count  //посчитали усредненный цвет
                    sumB/=count
                    for (i in s1 until f1) {
                        for (j in s2 until f2) {
                            var  R = (sumR * weights[i - s1][j - s2]).toInt() + (Color.red(pixels[j * width + i]) * (1 - weights[i - s1][j - s2]))
                            var  G = (sumG * weights[i - s1][j - s2]).toInt() + (Color.green(pixels[j * width + i]) * (1 - weights[i - s1][j - s2]))
                            var  B = (sumB * weights[i - s1][j - s2]).toInt() + (Color.blue(pixels[j * width + i]) * (1 - weights[i - s1][j - s2]))
                            R = Math.max(0.0, Math.min(255.0, R))
                            G = Math.max(0.0, Math.min(255.0, G))
                            B = Math.max(0.0, Math.min(255.0, B))
                            pixels[j * width + i] = 255 shl 24 or (R.toInt() shl 16) or (G.toInt() shl 8) or B.toInt()
                        }
                    }
                    tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                    retouchView.setImageBitmap(tmpImage)

                }
                return true
            }})
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_retouch, container, false)
    }
    override fun onStop() {
        super.onStop()
    }
}