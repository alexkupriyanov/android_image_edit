package com.example.lbg99.andrapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import com.example.lbg99.andrapp.R.id.*
import com.example.lbg99.andrapp.commonData.Companion.imageBitmap
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.photoImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    val TAG = "fragmentFilters"
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 2
    var mCurrentPhotoPath: String? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun photoFromGallery() {
        val callGalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        callGalleryIntent.type = "image/*"
        if (callGalleryIntent.resolveActivity(activity?.packageManager) != null) {
            startActivityForResult(callGalleryIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun takeAndSetPhoto(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val auth: String = activity?.packageName + ".fileprovider"
                val photoURI = FileProvider.getUriForFile(context!!,
                        auth,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super .onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            imageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImage)
            commonData.imageBitmap = imageBitmap
            if (commonData.imageBitmap!!.width > resources.getDimension(R.dimen.width) || commonData.imageBitmap!!.height > resources.getDimension(R.dimen.height)) {
                commonData.imageBitmap = Zoom(Math.min(resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.width,
                        resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.height), commonData.imageBitmap)
            }
            photoImageView.setImageBitmap(imageBitmap)


        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            commonData.imageBitmap = imageBitmap
            if (commonData.imageBitmap!!.width > resources.getDimension(R.dimen.width) || commonData.imageBitmap!!.height > resources.getDimension(R.dimen.height)) {
                commonData.imageBitmap = Zoom(Math.min(resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.width,
                        resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.height), commonData.imageBitmap)
            }
            photoImageView.setImageBitmap(imageBitmap)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()



        photoImageView.setImageBitmap(commonData.imageBitmap)

        if (commonData.imageBitmap!!.width > resources.getDimension(R.dimen.width) || commonData.imageBitmap!!.height > resources.getDimension(R.dimen.height)) {
            commonData.imageBitmap = Zoom(Math.min(resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.width,
                    resources.getDimension(R.dimen.width).toDouble() / commonData.imageBitmap!!.height), commonData.imageBitmap)
        }

        fab.setOnClickListener {
            val content = arrayOf(getString(R.string.get_photo), getString(R.string.get_image))
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.take_quest)
                    .setItems(content, DialogInterface.OnClickListener { dialog, which ->
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0) {
                            takeAndSetPhoto()
                        }
                        if (which == 1) {
                            photoFromGallery()
                        }
                        commonData().saveChange()
                    })
            builder.show()
        }

        saveBtn.setOnClickListener {
            commonData().saveChange()
            Toast.makeText(context, "Save complete!", Toast.LENGTH_SHORT).show()
        }
    }

    fun Zoom(value: Double, zoomImage: Bitmap?): Bitmap? {
        //инициализация переменных перед преобазованием
        val w = zoomImage!!.getWidth()
        val h = zoomImage!!.getHeight()
        val pixels = IntArray(w * h)
        zoomImage!!.getPixels(pixels, 0, w, 0, 0, w, h)
        //растяжение/сжатие
        val newPixels: IntArray
        newPixels = trilinear_filt(value, value, w, h, pixels)
        val w2 = Math.abs((w * value).toInt())
        val h2 = Math.abs((h * value).toInt())
        //обновление currentBitmap
        return Bitmap.createBitmap(newPixels, w2, h2, Bitmap.Config.ARGB_8888)
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