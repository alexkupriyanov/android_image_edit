package com.example.lbg99.andrapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_zoom.*

class ZoomFragment : Fragment() {

    var tmpImage: Bitmap? = null
    var zoom: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        tmpImage = commonData.imageBitmap
        zoomView.setImageBitmap(commonData.imageBitmap)
        zoomSeek.progress = zoomSeek.max / 2
        resizeValue.text = (zoomSeek.progress - zoomSeek.max / 2 + 1).toString()
        zoomSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress >= zoomSeek.max / 2) {
                    resizeValue.text = ((progress - zoomSeek.max / 2).toDouble() / 10 + 1).toString()
                    zoom = resizeValue.text.toString().toDouble()
                } else {
                    val pre = "1/"
                    val post = ((zoomSeek.max / 2 - progress).toDouble() / 10 + 1).toString()
                    resizeValue.text = "$pre$post"
                    zoom = 1.0 / post.toDouble()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (zoom != 1.0) {
                    tmpImage = Zoom(zoom * 100.0, commonData.imageBitmap)
                    zoomView.setImageBitmap(tmpImage)
                }
                else
                    zoomView.setImageBitmap(commonData.imageBitmap)
            }
        })
    }

    fun Zoom(value: Double, zoomImage: Bitmap?): Bitmap? {
        var value = value
        //инициализация переменных перед преобазованием
        value /= 100.0
        val w = zoomImage!!.getWidth()
        val h = zoomImage!!.getHeight()
        val pixels = IntArray(w * h)
        zoomImage!!.getPixels(pixels, 0, w, 0, 0, w, h)
        //растяжение/сжатие
        val newPixels: IntArray
        if (Math.abs(value) > 1) {
            newPixels = bilinear_filt(value, value, w, h, pixels)
        } else {
            newPixels = trilinear_filt(value, value, w, h, pixels)
        }
        val w2 = Math.abs((w * value).toInt())
        val h2 = Math.abs((h * value).toInt())
        //обновление currentBitmap
        return Bitmap.createBitmap(newPixels, w2, h2, Bitmap.Config.ARGB_8888)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_zoom, container, false)
    }

    override fun onStop() {
        super.onStop()
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