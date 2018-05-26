package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.SeekBar
import com.example.lbg99.andrapp.R.id.*
import com.example.lbg99.andrapp.commonData.Companion.scaleFactor
import kotlinx.android.synthetic.main.fragment_turn.*
import kotlinx.android.synthetic.main.fragment_unsharp_masking.*

class TurnFragment : Fragment() {

    var tmpImage : Bitmap? = null
    var tmpPreview : Bitmap? = null
    var prevX: Double? = null
    var prevY: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_turn, container, false)
    }
    override fun onStart() {
        super.onStart()
        cancelTurnBtn.setOnClickListener {
            turnView.setImageBitmap(commonData.imageBitmap)
            tmpImage = commonData.imageBitmap
        }
        picker.minValue = 0
        picker.maxValue = 360

        picker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener{
            override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
                turnPreviewImage.setImageBitmap(Rotate(picker!!.value.toDouble(), tmpPreview))

            }
        })

        doTurnBtn.setOnClickListener {
            tmpImage = Rotate(picker!!.value.toDouble(), commonData.imageBitmap)
            turnView.setImageBitmap(tmpImage)
        }

        val targetW = 100
        val targetH = 100
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        val photoW = commonData.imageBitmap!!.width
        val photoH = commonData.imageBitmap!!.height
        if (photoH > targetH || photoW > targetW) {
            val scaleFactor = Math.max(photoW / targetW, photoH / targetH)
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true
            tmpPreview = BitmapFactory.decodeFile(commonData.currentPhotoPath, bmOptions)
            Log.i(">>>>>", "mBitmap.getWidth()=" + commonData.imageBitmap!!.width)
            Log.i(">>>>>", "mBitmap.getHeight()=" + commonData.imageBitmap!!.height)
        }

        applyTurnBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }
        tmpImage = commonData.imageBitmap
        turnView.setImageBitmap(tmpImage)

    }
    fun Rotate(value: Double, bitmap: Bitmap?) : Bitmap {
        var value = Math.toRadians(-value)
        //инициализация переменных перед преобазованием
        val w = bitmap!!.width
        val h = bitmap!!.height
        val pixels = IntArray(w * h)
        bitmap!!.getPixels(pixels, 0, w, 0, 0, w, h)
        val newWidth = (h * Math.abs(Math.sin(value)) + w * Math.abs(Math.cos(value))).toInt()
        val newHeight = (h * Math.abs(Math.cos(value)) + w * Math.abs(Math.sin(value))).toInt()
        val newPixels = IntArray(newHeight * newWidth)
        val oX = w / 2
        val oY = h / 2 //центр исходной картинки
        val dX = newWidth / 2 - oX //сдвиг центра
        val dY = newHeight / 2 - oY //сдвиг центра
        var pixelA: Int
        var pixelB: Int
        var pixelC: Int
        var pixelD: Int
        var x: Int
        var y: Int
        //вычисление новых пикселей
        for (i in 0 until newHeight) {
            for (j in 0 until newWidth) {
                //исходные координаты
                x = (oX + (j.toDouble() - oX - dX) * Math.cos(value) - (i.toDouble() - oY - dY) * Math.sin(value)).toInt()
                y = (oY + (j.toDouble() - oX - dX) * Math.sin(value) + (i.toDouble() - oY - dY) * Math.cos(value)).toInt()
                if (y > 0 && x > 0 && y < h - 2 && x < w - 2) {
                    //усреднение пикселей
                    pixelA = pixels[y * w + x]
                    pixelB = pixels[y * w + x + 1]
                    pixelC = pixels[(y + 1) * w + x]
                    pixelD = pixels[(y + 1) * w + x + 1]
                    val R = (Color.red(pixelA) + Color.red(pixelB) + Color.red(pixelC) + Color.red(pixelD)) / 4
                    val G = (Color.green(pixelA) + Color.green(pixelB) + Color.green(pixelC) + Color.green(pixelD)) / 4
                    val B = (Color.blue(pixelA) + Color.blue(pixelB) + Color.blue(pixelC) + Color.blue(pixelD)) / 4
                    val Al = (Color.alpha(pixelA) + Color.alpha(pixelB) + Color.alpha(pixelC) + Color.alpha(pixelD)) / 4

                    newPixels[i * newWidth + j] = Al * 16777216 + R * 65536 + G * 256 + B
                } else {
                    newPixels[i * newWidth + j] = 0x00FFFFFF
                }
            }
        }
        return Bitmap.createBitmap(newPixels, newWidth, newHeight, Bitmap.Config.RGB_565)
    }
    override fun onStop() {
        super.onStop()
    }
}