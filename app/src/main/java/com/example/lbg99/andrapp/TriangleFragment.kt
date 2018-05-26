package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_triangle.*
import kotlinx.android.synthetic.main.fragment_zoom.*
class TriangleFragment : Fragment() {

    var tmpImage: Bitmap? = null
    var number = 0
    var textViews = arrayOfNulls<TextView>(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        tmpImage = commonData.imageBitmap
        cancelTriangleBtn.setOnClickListener {
            triangleView.setImageBitmap(commonData.imageBitmap)
            tmpImage = commonData.imageBitmap
            commentText.text = ""
            clear(number, textViews)
        }

        applyTriangleBtn.setOnClickListener {
            commonData.imageBitmap = tmpImage
        }

        setPointsBtn.setOnClickListener {
            workWithTriangles()
        }

        deletePointsBtn.setOnClickListener {
            clear(number, textViews)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_triangle, container, false)
    }
    override fun onStop() {
        super.onStop()
    }

    fun workWithTriangles() {
        // инициализация
        val relativeLayout = layout1
        number = 0
        commentText.text = "Выберите три точки начального треугольника"
        triangleView.setOnTouchListener(object : View.OnTouchListener {

            var coorX = DoubleArray(6)
            var coorY = DoubleArray(6)
            var index = IntArray(6)
            override fun onTouch(v: View, event: MotionEvent): Boolean { //при касании
                if (event.action == MotionEvent.ACTION_UP) {
                    if (number < 6) { // если ещё не все точки назначены

                        val x = event.x.toInt()//запомним координаты
                        val y = event.y.toInt()
                        coorX[number] = x.toDouble()
                        coorY[number] = y.toDouble()
                        //добавление циферки на экран:
                        val mView = TextView(context)
                        textViews[number] = mView
                        val text: String
                        if (number < 3) {
                            text = "▼"
                        } else {
                            text = "▲"
                        }
                        textViews[number]!!.setText("" + text)
                        val id = View.generateViewId()
                        textViews[number]!!.setId(id)
                        index[number] = id
                        textViews[number]!!.setTextColor(resources.getColor(R.color.black))
                        val layoutParams = ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                ConstraintLayout.LayoutParams.MATCH_PARENT)
                        layoutParams.setMargins(x, y, 0, 0)
                        relativeLayout.addView(textViews[number], layoutParams)
                        number++
                    }
//обновление текста после ввода трёх точек
                    if (number == 3) {
                        commentText.text = "Выберите три точки конечного треугольника"
                    }
//вычисление коэффициентов после ввода шести точек
                    if (number == 6) {
                        commentText.text = ""
                        triangl(coorX, coorY)
                        triangleView.setImageBitmap(tmpImage)
                        clear(number, textViews)
                    }
                }
                return true
            }
        })
    }

    fun clear (cnt : Int, index : Array<TextView?>) {
        for (i in 0 until cnt) {
            val curView = index[i]
            layout1.removeView(curView)
        }
        number = 0
    }

    fun triangl(coordX: DoubleArray, coordY: DoubleArray) {
        //вичислим дельту, чтобы решить систему уравнений, узнать коэффициенты матрицы преобразований
        var delta = coordX[0] * coordY[1] + coordX[1] * coordY[2] + coordX[2] * coordY[0] - coordX[2] * coordY[1] - coordX[1] * coordY[0] - coordX[0] * coordY[2]


        //выглядит громоздко, но просто по формуле считаем для каждого коэф-фа дельту по формуле
        var delta_a11 = coordX[3] * coordY[1] + coordX[4] * coordY[2] + coordX[5] * coordY[0] - coordX[5] * coordY[1] - coordX[4] * coordY[0] - coordX[3] * coordY[2]
        var delta_a21 = coordX[0] * coordX[4] + coordX[1] * coordX[5] + coordX[2] * coordX[3] - coordX[2] * coordX[4] - coordX[1] * coordX[3] - coordX[0] * coordX[5]
        var delta_a31 = coordX[0] * coordY[1] * coordX[5] + coordX[1] * coordY[2] * coordX[3] + coordX[2] * coordY[0] * coordX[4] - coordX[2] * coordY[1] * coordX[3] - coordX[1] * coordY[0] * coordX[5] - coordX[0] * coordY[2] * coordX[4]
        var delta_a12 = coordY[3] * coordY[1] + coordY[4] * coordY[2] + coordY[5] * coordY[0] - coordY[5] * coordY[1] - coordY[4] * coordY[0] - coordY[3] * coordY[2]
        var delta_a22 = coordX[0] * coordY[4] + coordX[1] * coordY[5] + coordX[2] * coordY[3] - coordX[2] * coordY[4] - coordX[1] * coordY[3] - coordX[0] * coordY[5]
        var delta_a32 = coordX[0] * coordY[1] * coordY[5] + coordX[1] * coordY[2] * coordY[3] + coordX[2] * coordY[0] * coordY[4] - coordX[2] * coordY[1] * coordY[3] - coordX[1] * coordY[0] * coordY[5] - coordX[0] * coordY[2] * coordY[4]


        var a11 = (delta_a11.toDouble() / delta.toDouble())
        var a21 = delta_a21 / delta
        var a31 = delta_a31 / delta
        var a12 = delta_a12 / delta
        var a22 = delta_a22 / delta
        var a32 = delta_a32 / delta

        //посчитали детерминатн матрицы
        var detM = a11 * a22 - a12 * a21

        //вычисление коэффициентов с помощью формулы  https://habr.com/post/278597/
        val alpha: Double
        var sy: Double
        var sx: Double
        var hx: Double

        if (a22 == 0.0) {
            alpha = Math.PI / 2
            sy = (-a21).toDouble()
        }
        else {
            alpha = Math.atan((-a21 / a22).toDouble())
            sy = a22 / Math.cos(alpha)
        }

        sx = detM / sy

        if(detM==0.0) detM=1.0 //что бы не делить на 0, это, конечно, не особо возможно
        //но мало ли...

        hx = ((a11 * a21 + a12 * a22) / detM).toDouble()

        val w = tmpImage!!.width
        val h = tmpImage!!.height
        val pixel = IntArray(w * h)
        tmpImage!!.getPixels(pixel, 0, w, 0, 0, w, h)

        //либо билинейное, либо трилинейное растяжение сжатие
        var newPixels: IntArray
        if (Math.abs(sx) * Math.abs(sy) > 1) {
            newPixels = bilinear_filt(sx, sy, w, h, pixel)
        }
        else {
            newPixels = trilinear_filt(sx, sy, w, h, pixel)
        }

        var w2 = Math.abs((w * sx).toInt())
        var h2 = Math.abs((h * sy).toInt())

        //вызов функции перемещения изображения
        newPixels = perem(hx, 0.0, w2, h2, newPixels)
        //поворот
        tmpImage = Rotate(alpha, tmpImage)
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

    // описание функции перемещения
    fun perem(tx: Double, ty: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var peremX = tx.toInt()
        var peremY = ty.toInt()
        var newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                if (i - peremY < h && i - peremY >= 0 && j - peremX < w && j - peremX >= 0) {
                    newPixels[i * w + j] = pixels[(i - peremY) * w + (j - peremX)]
                }
                else {
                    newPixels[i * w + j] = 0x00ffffff
                }
            }
        }
        return newPixels
    }

    fun bilinear_filt(sx: Double, sy: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var sx = sx
        var sy = sy
        var pixels = pixels

        if (sx < 0) {
            pixels = rotateVert(w, h, pixels)
            sx = -sx
        }
        if (sy < 0) {
            pixels = rotateHoriz(w, h, pixels)
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

    fun trilinear_filt(sx: Double, sy: Double, w: Int, h: Int, pixels: IntArray): IntArray {
        var sx = sx
        var sy = sy
        var pixels = pixels
        if (sx < 0) {
            pixels = rotateVert(w, h, pixels)
            sx = -sx
        }
        if (sy < 0) {
            pixels = rotateHoriz(w, h, pixels)
            sy = -sy
        }
        val pixels2 = bilinear_filt(0.5, 0.5, w, h, pixels)
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
        var new_i: Int

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

        var newRed: Int
        var newGreen: Int
        var newBlue: Int

        for (i in 0 until height) {
            for (j in 0 until width) {
                new_i = i * width + j
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

                //наконец получаем три итоговых значения
                newRed = (Ared.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) + Bred.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cred.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) + Dred.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Ered.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff + Fred.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Gred.toDouble() * (1 - w2_diff) * h2_diff * h3_diff + Hred.toDouble() * w2_diff * h2_diff * h3_diff).toInt()

                newGreen = (Agreen.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) + Bgreen.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cgreen.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) + Dgreen.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Egreen.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff + Fgreen.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Ggreen.toDouble() * (1 - w2_diff) * h2_diff * h3_diff + Hgreen.toDouble() * w2_diff * h2_diff * h3_diff).toInt()

                newBlue = (Ablue.toDouble() * (1 - w_diff) * (1 - h_diff) * (1 - h3_diff) + Bblue.toDouble() * w_diff * (1 - h_diff) * (1 - h3_diff) +
                        Cblue.toDouble() * (1 - w_diff) * h_diff * (1 - h3_diff) + Dblue.toDouble() * w_diff * h_diff * (1 - h3_diff) +
                        Eblue.toDouble() * (1 - w2_diff) * (1 - h2_diff) * h3_diff + Fblue.toDouble() * w2_diff * (1 - h2_diff) * h3_diff +
                        Gblue.toDouble() * (1 - w2_diff) * h2_diff * h3_diff + Hblue.toDouble() * w2_diff * h2_diff * h3_diff).toInt()

                // и записываем их
                newPixels[new_i] = 255 * 16777216 + newRed * 65536 + newGreen * 256 + newBlue
            }
        }
        return newPixels
    }

    //поворот по вертикали
    fun rotateVert(w: Int, h: Int, pixels: IntArray): IntArray {
        val newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                newPixels[i * w + j] = pixels[i * w + (w - 1 - j)]
            }
        }
        return newPixels
    }

    //поворот по горизонтали
    fun rotateHoriz(w: Int, h: Int, pixels: IntArray): IntArray {
        val newPixels = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                newPixels[i * w + j] = pixels[(h - 1 - i) * w + j]
            }
        }
        return newPixels
    }
}