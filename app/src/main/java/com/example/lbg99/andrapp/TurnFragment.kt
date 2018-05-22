package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.SeekBar
import com.example.lbg99.andrapp.R.id.*
import com.example.lbg99.andrapp.commonData.Companion.scaleFactor
import kotlinx.android.synthetic.main.fragment_turn.*
import kotlinx.android.synthetic.main.fragment_unsharp_masking.*

class TurnFragment : Fragment() {

    var tmpImage : Bitmap? = null
    var tmpPreview : Bitmap? = null
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

        doTurnBtn.setOnClickListener {
           tmpImage = Rotate(picker.value.toDouble(), commonData.imageBitmap)
            turnView.setImageBitmap(tmpImage)
        }

        applyTurnBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }
        turnPreviewImage.setImageBitmap(tmpPreview)
        tmpImage = commonData.imageBitmap
        turnView.setImageBitmap(tmpImage)

    }
    fun Rotate(value: Double, bitmap: Bitmap?) : Bitmap {
        var value = value
        value = Math.toRadians(value)
        //инициализация переменных перед преобазованием
        val w = bitmap!!.width
        val h = bitmap!!.height
        val pixels = IntArray(w * h)
        bitmap!!.getPixels(pixels, 0, w, 0, 0, w, h)
        //поворот currentBitmap
        val tmp = rotate(value, w, h, pixels)
        return tmp
    }

    /** повораичвает изображение на заданный угол  */
    //сейчас использует встроенные средства; возможно, это надо исправить
    fun rotate(alpha: Double, w: Int, h: Int, pixels: IntArray): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(Math.toDegrees(alpha).toFloat())
        var temp = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
        temp = Bitmap.createBitmap(temp, 0, 0, w, h, matrix, true)
        return temp
    }
    override fun onStop() {
        super.onStop()
    }
}