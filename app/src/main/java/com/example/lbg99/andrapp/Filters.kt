package com.example.lbg99.andrapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import com.example.lbg99.andrapp.Filters.ConvolutionMatrix.Companion.computeConvolution
import kotlinx.android.synthetic.main.activity_filters.*
import kotlin.math.*
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.graphics.Canvas
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.widget.TextView

import android.graphics.RectF
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth

import android.R.attr.radius



import android.graphics.Color
import android.graphics.Paint
import java.nio.file.Files.move
import android.R.attr.radius




//import sun.swing.SwingUtilities2.drawRect






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
    private val bitmapCanvas: Canvas? = null

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
                Image.setImageBitmap(applyGaussianBlur(tmpImage!!, 3,0.0,2.0))

               }
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                val number = findViewById(R.id.seekBar) as SeekBar
                val value = (number.progress - 180).toDouble()
                try {
                    Rotate(value)
                } catch (e: OutOfMemoryError) { //если недостаточно памяти
                    Toast.makeText(applicationContext, "Недостаточно памяти для выполнения операции", Toast.LENGTH_SHORT).show()
                }

                val imageview = findViewById(R.id.Image) as ImageView
                imageview.setImageBitmap(tmpImage)

            }




})
        trigl.setOnClickListener{
            workWithTriangles()
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
                result.setPixel(x + 1, y + 1, Color.argb(255, R, G, B));
            }
            }

        //imageview.setImageBitmap(result)
        return result
    }


    /** Поворачивает изображение  */
    fun Rotate(value: Double) {
        var value = value
        value = Math.toRadians(value)
        //инициализация переменных перед преобазованием
        val w = tmpImage!!.width
        val h = tmpImage!!.height
        val pixels = IntArray(w * h)
        tmpImage!!.getPixels(pixels, 0, w, 0, 0, w, h)
        //поворот currentBitmap
        tmpImage = rotate(value, w, h, pixels)
    }


    fun rotate(alpha: Double, w: Int, h: Int, pixels: IntArray): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(Math.toDegrees(alpha).toFloat())
        var temp = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888)
        temp = Bitmap.createBitmap(temp, 0, 0, w, h, matrix, true)
        return temp
    }
    var numberOfPoints: Int = 0
    @SuppressLint("ClickableViewAccessibility")
    var corx = 0f
    var cory = 0f
    private lateinit var background: Canvass
    @SuppressLint("ClickableViewAccessibility")
    fun workWithTriangles() {
        val imageview = findViewById<View>(R.id.Image) as ImageView
        numberOfPoints = 0
        val textview = findViewById<View>(R.id.text) as TextView
        textview.text = "Назначьте три точки исходного треугольника [1-2-3]"
        imageview.setOnTouchListener(object : View.OnTouchListener {
            var points = arrayOfNulls<TextView>(6)
            var pointsX = DoubleArray(6)
            var pointsY = DoubleArray(6)
            internal var ids = IntArray(6)
            @SuppressLint("WrongViewCast")
            override fun onTouch(v: View, event: MotionEvent): Boolean { //при касании
                if (event.action == MotionEvent.ACTION_UP) { //в момент прекращения касания
                    if (numberOfPoints < 6) { //если меньше шести точек
                        val x = event.x.toInt()
                        val y = event.y.toInt()
                        corx= x.toFloat()
                        cory= y.toFloat()
                        pointsX[numberOfPoints] = x.toDouble()
                        pointsY[numberOfPoints] = y.toDouble()
                        //добавление циферки на экран:
                        points[numberOfPoints] = TextView(getApplicationContext())
                        var  txt = numberOfPoints + 1
                        val layout1 = findViewById(R.id.layout1) as android.support.constraint.ConstraintLayout
                        val canvass = Canvass(getApplicationContext())
                        layout1.addView(canvass)

                        numberOfPoints++
                    }
                    //обновление текста после ввода трёх точек
                    if (numberOfPoints == 3) {
                        textview.text = "Назначьте три точки конечного треугольника [4-5-6]"
                    }
                    //вычисление коэффициентов после ввода шести точек
                    if (numberOfPoints == 6) {
                        textview.text = "" //удаление текстовой подсказки

                            triangl(pointsX, pointsY)


                        imageview.setImageBitmap(tmpImage)
                        numberOfPoints++

                    }
                }
                return true
            }
        })
    }

    inner class Canvass(context: Context) : View(context) {

        override fun onDraw(canvas: Canvas) {
            val paint = Paint()
            canvas.drawCircle(corx, cory, 5f, paint)
        }
    }

    fun triangl(pointsX: DoubleArray, pointsY: DoubleArray) {
        //вычисление элементов матрицы (мне тоже страшно от этого кода)
        var delta = pointsX[0] * pointsY[1] + pointsX[1] * pointsY[2] + pointsX[2] * pointsY[0] - pointsX[2] * pointsY[1] - pointsX[1] * pointsY[0] - pointsX[0] * pointsY[2]
        var delta_a11 = pointsX[3] * pointsY[1] + pointsX[4] * pointsY[2] + pointsX[5] * pointsY[0] - pointsX[5] * pointsY[1] - pointsX[4] * pointsY[0] - pointsX[3] * pointsY[2]
        var delta_a21 = pointsX[0] * pointsX[4] + pointsX[1] * pointsX[5] + pointsX[2] * pointsX[3] - pointsX[2] * pointsX[4] - pointsX[1] * pointsX[3] - pointsX[0] * pointsX[5]
        var delta_a31 = pointsX[0] * pointsY[1] * pointsX[5] + pointsX[1] * pointsY[2] * pointsX[3] + pointsX[2] * pointsY[0] * pointsX[4] -
                pointsX[2] * pointsY[1] * pointsX[3] - pointsX[1] * pointsY[0] * pointsX[5] - pointsX[0] * pointsY[2] * pointsX[4]
        var delta_a12 = pointsY[3] * pointsY[1] + pointsY[4] * pointsY[2] + pointsY[5] * pointsY[0] - pointsY[5] * pointsY[1] - pointsY[4] * pointsY[0] - pointsY[3] * pointsY[2]
        var delta_a22 = pointsX[0] * pointsY[4] + pointsX[1] * pointsY[5] + pointsX[2] * pointsY[3] - pointsX[2] * pointsY[4] - pointsX[1] * pointsY[3] - pointsX[0] * pointsY[5]
        var delta_a32 = pointsX[0] * pointsY[1] * pointsY[5] + pointsX[1] * pointsY[2] * pointsY[3] + pointsX[2] * pointsY[0] * pointsY[4] -
                pointsX[2] * pointsY[1] * pointsY[3] - pointsX[1] * pointsY[0] * pointsY[5] - pointsX[0] * pointsY[2] * pointsY[4]
        var a11 = (delta_a11.toDouble() / delta.toDouble())
        var a21 = delta_a21 / delta
        var a31 = delta_a31 / delta
        var a12 = delta_a12 / delta
        var a22 = delta_a22 / delta
        var a32 = delta_a32 / delta
        var detM = a11 * a22 - a12 * a21
        //вычисление коэффициентов по числам в матрице, формулы с хабра
        val alpha: Double
        var sy: Double
        var sx: Double
        var hx: Double

        if (a22 == 0.0) {
            alpha = Math.PI / 2
            sy = (-a21).toDouble()
        } else {
            alpha = Math.atan((-a21 / a22).toDouble())
            sy = a22 / Math.cos(alpha)
        }
        sx = detM / sy
        if(detM==0.0) detM=1.0
        hx = ((a11 * a21 + a12 * a22) / detM).toDouble()
        //инициализация переменных перед преобазованием
        val w = tmpImage!!.width
        val h = tmpImage!!.height
        val pixel = IntArray(w * h)
        tmpImage!!.getPixels(pixel, 0, w, 0, 0, w, h)
        //растяжение/сжатие
        var newPixels: IntArray
        if (Math.abs(sx) * Math.abs(sy) > 1) {
            newPixels = bilinear(sx, sy, w, h, pixel)
        } else {
            newPixels = trilinear(sx, sy, w, h, pixel)
        }
        var w2 = Math.abs((w * sx).toInt())
        var h2 = Math.abs((h * sy).toInt())
        //сдвиг
        newPixels = move(hx, 0.0, w2, h2, newPixels)
        //поворот
        var temp = rotate(alpha, w2, h2, newPixels)
        var w3 = temp.width
        var h3 = temp.height
        var tempPixels = IntArray(w3 * h3)
        temp.getPixels(tempPixels, 0, w3, 0, 0, w3, h3)
        //обновление currentBitmap
        tmpImage = Bitmap.createBitmap(tempPixels, w3, h3, Bitmap.Config.ARGB_8888)
    }

    fun move(tx: Double, ty: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var moveX = tx.toInt()
        var moveY = ty.toInt()
        var newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                if (i - moveY < h && i - moveY >= 0 && j - moveX < w && j - moveX >= 0) {
                    newPixels[i * w + j] = pixels[(i - moveY) * w + (j - moveX)]
                } else {
                    newPixels[i * w + j] = 0x00ffffff
                }
            }
        }
        return newPixels
    }

    fun bilinear(sx: Double, sy: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var sx = sx
        var sy = sy
        var pixels = pixels
        if (sx < 0) {
            pixels = verticalFlip(w, h, pixels)
            sx = -sx
        }
        if (sy < 0) {
            pixels = horizontalFlip(w, h, pixels)
            sy = -sy
        }
        var w2 = (w * sx).toInt()
        var h2 = (h * sy).toInt()
        var newPixels = IntArray(w2 * h2)
        var x_diff: Double
        var y_diff: Double
        var A: Int
        var B: Int
        var C: Int
        var D: Int
        var x: Int
        var y: Int
        var Agreen: Int
        var Ablue: Int
        var Ared: Int
        var Bgreen: Int
        var Bblue: Int
        var Bred: Int
        var Cgreen: Int
        var Cblue: Int
        var Cred: Int
        var Dgreen: Int
        var Dblue: Int
        var Dred: Int
        var newGreen: Int
        var newBlue: Int
        var newRed: Int
        val newAlpha: Int
        sx = 1 / sx
        sy = 1 / sy
        for (i in 0 until h2) {
            for (j in 0 until w2) {
                y = (sy * i).toInt()
                x = (sx * j).toInt()
                x_diff = sx * j - x
                y_diff = sy * i - y
                A = pixels[y * w + x]
                B = pixels[y * w + (x + 1) % w]
                C = pixels[(y + 1) % h * w + x]
                D = pixels[(y + 1) % h * w + (x + 1) % w]
                Ared = (A and 0x00ff0000) / 65536
                Agreen = (A and 0x0000ff00) / 256
                Ablue = A and 0x000000ff
                Bred = (B and 0x00ff0000) / 65536
                Bgreen = (B and 0x0000ff00) / 256
                Bblue = B and 0x000000ff
                Cred = (C and 0x00ff0000) / 65536
                Cgreen = (C and 0x0000ff00) / 256
                Cblue = C and 0x000000ff
                Dred = (D and 0x00ff0000) / 65536
                Dgreen = (D and 0x0000ff00) / 256
                Dblue = D and 0x000000ff
                newRed = (Ared.toDouble() * (1 - x_diff) * (1 - y_diff) + Bred.toDouble() * x_diff * (1 - y_diff) + Cred.toDouble() * (1 - x_diff) * y_diff + Dred.toDouble() * x_diff * y_diff).toInt()
                newGreen = (Agreen.toDouble() * (1 - x_diff) * (1 - y_diff) + Bgreen.toDouble() * x_diff * (1 - y_diff) + Cgreen.toDouble() * (1 - x_diff) * y_diff + Dgreen.toDouble() * x_diff * y_diff).toInt()
                newBlue = (Ablue.toDouble() * (1 - x_diff) * (1 - y_diff) + Bblue.toDouble() * x_diff * (1 - y_diff) + Cblue.toDouble() * (1 - x_diff) * y_diff + Dblue.toDouble() * x_diff * y_diff).toInt()
                newPixels[i * w2 + j] = 255 * 16777216 + newRed * 65536 + newGreen * 256 + newBlue
            }
        }
        return newPixels
    }

    fun trilinear(sx: Double, sy: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var sx = sx
        var sy = sy
        var pixels = pixels
        if (sx < 0) {
            pixels = verticalFlip(w, h, pixels)
            sx = -sx
        }
        if (sy < 0) {
            pixels = horizontalFlip(w, h, pixels)
            sy = -sy
        }
        val pixels2 = bilinear(0.5, 0.5, w, h, pixels)
        val w2 = (w * 0.5).toInt()
        val h2 = (h * 0.5).toInt()
        val width = (w * sx).toInt()
        val height = (h * sy).toInt()
        val newPixels = IntArray(width * height)
        val w_ratio = ((w - 1).toFloat() / width).toDouble()
        val h_ratio = ((h - 1).toFloat() / height).toDouble()
        val w2_ratio = ((w2 - 1).toFloat() / width).toDouble()
        val h2_ratio = ((h2 - 1).toFloat() / height).toDouble()
        val h3_diff = ((w - width) / (w - w2).toFloat()).toDouble()
        var x: Double
        var y: Double
        var w_diff: Double
        var h_diff: Double
        var x2: Double
        var y2: Double
        var w2_diff: Double
        var h2_diff: Double
        var A: Int
        var B: Int
        var C: Int
        var D: Int
        var E: Int
        var F: Int
        var G: Int
        var H: Int
        var index: Int
        var index2: Int
        var indexNew: Int
        var Ared: Int
        var Agreen: Int
        var Ablue: Int
        var Bred: Int
        var Bgreen: Int
        var Bblue: Int
        var Cred: Int
        var Cgreen: Int
        var Cblue: Int
        var Dred: Int
        var Dgreen: Int
        var Dblue: Int
        var Ered: Int
        var Egreen: Int
        var Eblue: Int
        var Fred: Int
        var Fgreen: Int
        var Fblue: Int
        var Gred: Int
        var Ggreen: Int
        var Gblue: Int
        var Hred: Int
        var Hgreen: Int
        var Hblue: Int
        val newAlpha: Int
        var newRed: Int
        var newGreen: Int
        var newBlue: Int
        for (i in 0 until height) {
            for (j in 0 until width) {
                indexNew = i * width + j
                x = w_ratio * j
                y = h_ratio * i
                w_diff = x - x.toInt()
                h_diff = y - y.toInt()
                index = y.toInt() * w + x.toInt()
                A = pixels[index]
                B = pixels[(index + 1) % (w * h)]
                C = pixels[(index + w) % (w * h)]
                D = pixels[(index + w + 1) % (w * h)]
                x2 = w2_ratio * j
                y2 = h2_ratio * i
                w2_diff = x2 - x2.toInt()
                h2_diff = y2 - y2.toInt()
                index2 = y2.toInt() * w2 + x2.toInt()
                E = pixels2[index2]
                F = pixels2[(index2 + 1) % (w2 * h2)]
                G = pixels2[(index2 + w) % (w2 * h2)]
                H = pixels2[(index2 + w + 1) % (w2 * h2)]
                Ared = (A and 0x00ff0000) / 65536
                Agreen = (A and 0x0000ff00) / 256
                Ablue = A and 0x000000ff
                Bred = (B and 0x00ff0000) / 65536
                Bgreen = (B and 0x0000ff00) / 256
                Bblue = B and 0x000000ff
                Cred = (C and 0x00ff0000) / 65536
                Cgreen = (C and 0x0000ff00) / 256
                Cblue = C and 0x000000ff
                Dred = (D and 0x00ff0000) / 65536
                Dgreen = (D and 0x0000ff00) / 256
                Dblue = D and 0x000000ff
                Ered = (E and 0x00ff0000) / 65536
                Egreen = (E and 0x0000ff00) / 256
                Eblue = E and 0x000000ff
                Fred = (F and 0x00ff0000) / 65536
                Fgreen = (F and 0x0000ff00) / 256
                Fblue = F and 0x000000ff
                Gred = (G and 0x00ff0000) / 65536
                Ggreen = (G and 0x0000ff00) / 256
                Gblue = G and 0x000000ff
                Hred = (H and 0x00ff0000) / 65536
                Hgreen = (H and 0x0000ff00) / 256
                Hblue = H and 0x000000ff
                newRed = (Ared.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) +
                        Bred.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cred.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) +
                        Dred.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Ered.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff +
                        Fred.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Gred.toDouble() * (1 - w2_diff) * h2_diff * h3_diff +
                        Hred.toDouble() * w2_diff * h2_diff * h3_diff).toInt()
                newGreen = (Agreen.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) +
                        Bgreen.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cgreen.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) +
                        Dgreen.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Egreen.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff +
                        Fgreen.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Ggreen.toDouble() * (1 - w2_diff) * h2_diff * h3_diff +
                        Hgreen.toDouble() * w2_diff * h2_diff * h3_diff).toInt()
                newBlue = (Ablue.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) +
                        Bblue.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cblue.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) +
                        Dblue.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Eblue.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff +
                        Fblue.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Gblue.toDouble() * (1 - w2_diff) * h2_diff * h3_diff +
                        Hblue.toDouble() * w2_diff * h2_diff * h3_diff).toInt()
                newPixels[indexNew] = 255 * 16777216 + newRed * 65536 + newGreen * 256 + newBlue
            }
        }
        return newPixels
    }

    /** переворачивает currentBitmap по вертикали  */
    fun verticalFlip(w: Int, h: Int, pixels: IntArray): IntArray {
        val newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                newPixels[i * w + j] = pixels[i * w + (w - 1 - j)]
            }
        }
        return newPixels
    }

    /** переворачивает currentBitmap по горизонтали  */
    fun horizontalFlip(w: Int, h: Int, pixels: IntArray): IntArray {
        val newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                newPixels[i * w + j] = pixels[(h - 1 - i) * w + j]
            }
        }
        return newPixels
    }
}