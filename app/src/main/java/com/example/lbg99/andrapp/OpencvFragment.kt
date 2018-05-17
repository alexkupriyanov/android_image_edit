package com.example.lbg99.andrapp

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_opencv.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class OpencvFragment : Fragment() {
    protected val TAG: String? = null
    private val img: Mat? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        opencvView.setImageBitmap(commonData.imageBitmap)
        opencvBtn.setOnClickListener {
            Utils.bitmapToMat(commonData.imageBitmap, img)
            val result = steptowatershed(img)
            Utils.matToBitmap(result, commonData.imageBitmap, true)
            opencvView.setImageBitmap(commonData.imageBitmap)
        }
    }

    private val mLoaderCallback = object : BaseLoaderCallback(activity!!.applicationContext) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")

                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, activity!!.applicationContext, mLoaderCallback)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_opencv, container, false)
    }

    fun steptowatershed(img: Mat?): Mat {
        val threeChannel = Mat()
        Imgproc.cvtColor(img, threeChannel, Imgproc.COLOR_BGR2GRAY)
        Imgproc.threshold(threeChannel, threeChannel, 100.0, 255.0, Imgproc.THRESH_BINARY)
        val fg = Mat(img?.size(), CvType.CV_8U)
        Imgproc.erode(threeChannel, fg, Mat())
        val bg = Mat(img?.size(), CvType.CV_8U)
        Imgproc.dilate(threeChannel, bg, Mat())
        Imgproc.threshold(bg, bg, 1.0, 128.0, Imgproc.THRESH_BINARY_INV)
        val markers = Mat(img?.size(), CvType.CV_8U, Scalar(0.0))
        Core.add(fg, bg, markers)
        var result1: Mat
        val segmenter = WatershedSegmenter()
        segmenter.setMarkers(markers)
        result1 = segmenter.process(img!!)
        return result1
    }

    inner class WatershedSegmenter {
        private var markers = Mat()
        fun setMarkers(markerImage: Mat) {

            markerImage.convertTo(markers, CvType.CV_32SC1)
        }

        fun process(image: Mat): Mat {
            Imgproc.watershed(image, markers)
            markers.convertTo(markers, CvType.CV_8U)
            return markers
        }
    }
}