package com.example.lbg99.andrapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.app_bar_filter.*

class filter : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var curPath: String? = null
    private var tmpImage: Bitmap? = null
    private var pixels: Array<IntArray>? = null
    companion object {
        var absolutePath: String? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        curPath = intent.getStringExtra(absolutePath)
        tmpImage = BitmapFactory.decodeFile(curPath)
        imageFilter.setImageBitmap(tmpImage)
        setSupportActionBar(toolbar)
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
            imageFilter.setImageBitmap(tmp)
        }

        inverseBtn.setOnClickListener {
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
            imageFilter.setImageBitmap(tmp)

        }

        grayBtn.setOnClickListener {

            var r: Int
            var g: Int
            var b: Int
            var Y: Double
            var I: Double
            var Q: Double
            var matrix = pixels
            for (i in 0 until tmpImage!!.width)
                for (j in 0 until tmpImage!!.height) {
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
            imageFilter.setImageBitmap(tmp)
        }
        sepiaBtn.setOnClickListener {
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
            imageFilter.setImageBitmap(tmp)

        }
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun getPixelsMatrix() { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr:Array<IntArray>? = Array(tmpImage!!.width, { IntArray(tmpImage!!.height) })
        for(i in 0 until tmpImage!!.width)
            for(j in 0 until tmpImage!!.height)
                arr!![i][j]= tmpImage!!.getPixel(i,j)
        pixels = arr // закинули в глобальный массив
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
        menuInflater.inflate(R.menu.filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
