package com.example.lbg99.andrapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import java.io.File


class MainActivity : AppCompatActivity() {

    var mImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mImageView = findViewById<View>(R.id.imageView) as ImageView
        logMemory()
        readImage()
        logMemory()
    }

    private fun readImage() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "map.jpg")
        val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
        Log.d("log", String.format("bitmap size = %sx%s, byteCount = %s",
                bitmap.width, bitmap.height,
                bitmap.byteCount / 1024))
        mImageView!!.setImageBitmap(bitmap)
    }

    private fun logMemory() {
        Log.i("log", String.format("Total memory = %s",
                (Runtime.getRuntime().totalMemory() / 1024).toInt()))
    }

    fun decodeSampledBitmapFromResource(path: String,
                                        reqWidth: Int, reqHeight: Int): Bitmap {

        // Читаем с inJustDecodeBounds=true для определения размеров
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        return BitmapFactory.decodeFile(path, options)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        // Реальные размеры изображения
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


}
