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
import android.widget.Toast
import com.example.lbg99.andrapp.R.id.retouchView
import kotlinx.android.synthetic.main.fragment_retouch.*
import kotlinx.android.synthetic.main.nav_header_nav_menu.*

class RetouchFragment : Fragment() {

    var tmpImage: Bitmap? = null
    var radius = 10
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
        retouchView.setOnTouchListener(object:View.OnTouchListener {
            override  fun  onTouch(v:View, event: MotionEvent):Boolean {

                if (event.getAction() === MotionEvent.ACTION_MOVE){
                    var x = event.x.toInt()
                    var y = event.y.toInt()

                    val m_size = 2 * radius + 1
                    var x1 = 0
                    var y1 = 0
                    var sum = 0.0
                    val width = tmpImage!!.width
                    val height = tmpImage!!.height
                    var pixels = IntArray(width * height)
                    tmpImage!!.getPixels(pixels, 0, width, 0, 0, width, height)
                    var sumR = 0
                    var sumG = 0
                    var sumB = 0
                    var count = 0
                    val weights = Array(m_size, {DoubleArray(m_size)})
                    if (x < 0 || y < 0 || x > width || y > height) return true
                    for (i in -radius until radius)// считаем коэффициент и количество пикселей в выбранном радиусе
                    {
                        for (j in -radius until radius)
                        {

                            val dX = i.toDouble()
                            val dY = j.toDouble()
                            val dR = radius.toDouble()
                            weights[x1][y1] = (Math.pow(Math.E, -((dX * dX + dY * dY) / (2.0 * dR * dR)))) * 0.25
                            y1++

                        }
                        y1 = 0
                        x1++
                    }
                    val s1= (x - radius).toInt()
                    val f1=(x + radius).toInt()
                    val s2= (y - radius).toInt()
                    val f2=(y + radius).toInt()
                    for (i in s1 until f1) {
                        for (j in s2 until f2) {
                            var h=Math.sqrt(((x - i) * (x - i) + (y - j) * (y - j)).toDouble()).toInt()
                            if (h <= radius && i > 0 && j > 0 && i < width && j < height) {
                                sumR += Color.red(pixels[j * width + i])
                                sumG += Color.green(pixels[j * width + i])   // считаем сумму для цветов
                                sumB += Color.blue(pixels[j * width + i])
                                count++
                            }
                        }
                    }
                    sumR/=count
                    sumG/=count  //посчитали усредненный цвет
                    sumB/=count
                    x1 = 0
                    y1 = 0
                    for (i in s1 until f1) {
                        for (j in s2 until f2) {
                            var h=Math.sqrt(((x - i) * (x - i) + (y - j) * (y - j)).toDouble()).toInt()
                            if (h <= radius && i > 0 && j > 0 && i < width && j < height)
                            {
                                var  R = ((sumR * weights[x1][y1])) + (Color.red(pixels[j * width + i]) * (1 - weights[x1][y1]))
                                var  G = (sumG * weights[x1][y1]) + (Color.red(pixels[j * width + i]) * (1 - weights[x1][y1]))
                                var  B = (sumB * weights[x1][y1]) + (Color.red(pixels[j * width + i]) * (1 - weights[x1][y1]))
                                y1++
                                val a = 255
                                val p = a shl 24 or (R.toInt() shl 16) or (G.toInt() shl 8) or B.toInt()
                                pixels[j * width + i] = p
                            }
                        }
                        y1=0
                        x1++
                    }
                    tmpImage = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
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