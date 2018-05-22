package com.example.lbg99.andrapp

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_nav_menu.*
import kotlinx.android.synthetic.main.app_bar_nav_menu.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import com.example.lbg99.andrapp.R.id.opencvView
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class commonData {
    companion object {
        var imageBitmap: Bitmap? = null
        var currentPhotoPath: String? = null
        var scaleFactor: Int = 1
    }
    fun saveChange() {
        val file = File(currentPhotoPath)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun init(newBitmap: Bitmap?) {
        imageBitmap = newBitmap
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/capture_photo")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val OutletFname = "Image-$timeStamp.jpg"
        val file = File(myDir, OutletFname)
        currentPhotoPath = file.absolutePath
        saveChange()
        fixSize()
    }
    private fun fixSize() {
        val targetW = 720
        val targetH = 1128
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        val photoW = imageBitmap!!.width
        val photoH = imageBitmap!!.height
        if (photoH > targetH || photoW > targetW) {
            scaleFactor = Math.max(photoW / targetW, photoH / targetH) + 1
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
            Log.i(">>>>>", "mBitmap.getWidth()=" + imageBitmap!!.width)
            Log.i(">>>>>", "mBitmap.getHeight()=" + imageBitmap!!.height)
            saveChange()
        }
    }
}

class nav_menu : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val manager = supportFragmentManager

    @Throws(IOException::class)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_menu)
        setSupportActionBar(toolbar)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),0)
        }
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        commonData().init(BitmapFactory.decodeResource(resources,R.mipmap.logo))
        addFragmentToActivity(MainFragment())
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MainFragment().onActivityResult(requestCode,resultCode,data)
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings ->{

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.filters -> {
                addFragmentToActivity(FilterFragment())
            }
            R.id.unsharp_masking -> {
                addFragmentToActivity(UnsharpMaskingFragment())
            }
            R.id.turn -> {
                addFragmentToActivity(TurnFragment())
            }
            R.id.zoom -> {
                addFragmentToActivity(ZoomFragment())
            }
            R.id.retouch -> {
                addFragmentToActivity(RetouchFragment())
            }
            R.id.opencv -> {
                addFragmentToActivity(OpencvFragment())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun addFragmentToActivity(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
