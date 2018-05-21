package com.example.lbg99.andrapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
            val bmp= MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImage)
            commonData().init(bmp)
            photoImageView.setImageBitmap(imageBitmap)

        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val bmp = BitmapFactory.decodeFile(mCurrentPhotoPath)
            commonData().init(bmp)
            photoImageView.setImageBitmap(imageBitmap)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        photoImageView.setImageBitmap(commonData.imageBitmap)
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