package com.example.lbg99.andrapp

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_opencv.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class OpencvFragment : Fragment() {
    private var img: Mat? = null
    private var touchCount: Int = 0
    private var tl: Point? = null
    private var br: Point? = null
    private var dlg: ProgressDialog? = null

    init {
        OpenCVLoader.initDebug()
        img = Mat()
        tl = Point()
        br = Point()
    }

    override fun onStart() {
        super.onStart()
        opencvView.setImageBitmap(commonData.imageBitmap)
        dlg = ProgressDialog(context)
        opencvView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                val cx = (opencvView.width - commonData.imageBitmap!!.width) / 2
                val cy = (opencvView.height - commonData.imageBitmap!!.height) / 2
                if (event!!.action === MotionEvent.ACTION_DOWN) {
                    if (touchCount === 0) {
                        tl!!.x = event!!.x.toDouble() - cx
                        tl!!.y = event!!.y.toDouble() - cy
                        touchCount++
                    } else if (touchCount === 1) {
                        br!!.x = event!!.x.toDouble() - cx
                        br!!.y = event!!.y.toDouble() - cy
                    }
                }
                return true
            }
        })
        opencvBtn.setOnClickListener{
            var img = Mat(commonData.imageBitmap!!.height,commonData.imageBitmap!!.width, CvType.CV_8UC3)
            Utils.bitmapToMat(commonData.imageBitmap,img)
            Imgproc.cvtColor(img,img,Imgproc.COLOR_RGBA2RGB)
            var firstMask = Mat()
            var bgModel = Mat()
            var fgModel = Mat()
            var source = Mat(1, 1, CvType.CV_8U, Scalar(3.0))
            val rect = Rect(tl, br)
            Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,5, Imgproc.GC_INIT_WITH_RECT)
            Core.compare(firstMask, source, firstMask, Core.CMP_EQ)
            var foreground = Mat(img.size(), CvType.CV_8UC3, Scalar(255.0, 255.0, 255.0))
            img.copyTo(foreground)
            Imgproc.blur(foreground,foreground,Size(10.0,10.0))
            img.copyTo(foreground,firstMask)
            Utils.matToBitmap(foreground,commonData.imageBitmap)
            opencvView.setImageBitmap(commonData.imageBitmap)
            firstMask.release()
            source.release()
            bgModel.release()
            fgModel.release()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_opencv, container, false)
    }
}