package com.example.lbg99.andrapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_biliner_trilinear.*
import kotlinx.android.synthetic.main.app_bar_biliner_trilinear.*
import android.widget.TextView
import android.widget.Toast
import android.widget.RelativeLayout
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_filters.*


class biliner_trilinear : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        var absolutePath: String? = null
    }


    var curPath: String? = null
    private var tmpImage: Bitmap? = null
    private var pixels: Array<IntArray>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biliner_trilinear)
        curPath = intent.getStringExtra(biliner_trilinear.absolutePath)
        tmpImage = BitmapFactory.decodeFile(curPath)
        Image.setImageBitmap(tmpImage)
        getPixelsMatrix()
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

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
        menuInflater.inflate(R.menu.biliner_trilinear, menu)
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
    fun getPixelsMatrix()
    { //получает матрицу пикселей из bitmap (просто интовые байты)
        var arr:Array<IntArray>? = Array(tmpImage!!.width, { IntArray(tmpImage!!.height) })
        for(i in 0 until tmpImage!!.width)
            for(j in 0 until tmpImage!!.height)
                arr!![i][j]= tmpImage!!.getPixel(i,j)
        pixels = arr // закинули в глобальный массив
    }
    var numberOfPoints: Int = 0
    fun workWithTriangles() {
        // инициализация
        val imageview = findViewById<View>(R.id.photoImageView) as ImageView
        numberOfPoints = 0
        val textview = findViewById<View>(R.id.text) as TextView
        textview.text = "Назначьте три точки исходного треугольника [1-2-3]"
        imageview.setOnTouchListener(object : View.OnTouchListener {
            internal var points = arrayOfNulls<TextView>(6)
            internal var pointsX = IntArray(6)
            internal var pointsY = IntArray(6)
            internal var ids = IntArray(6)
            override fun onTouch(v: View, event: MotionEvent): Boolean { //при касании
                if (event.action == MotionEvent.ACTION_UP) { //в момент прекращения касания
                    if (numberOfPoints < 6) { //если меньше шести точек
                        //сохранение координат:
                        val x = event.x.toInt()
                        val y = event.y.toInt()
                        pointsX[numberOfPoints] = x
                        pointsY[numberOfPoints] = y
                        //добавление циферки на экран:
                        points[numberOfPoints] = TextView(applicationContext)
                        val text = numberOfPoints + 1
                        points[numberOfPoints]!!.setText("" + text)
                        val id = View.generateViewId()
                        points[numberOfPoints]!!.setId(id)
                        ids[numberOfPoints] = id
                        val layoutParams = RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParams.setMargins(x, y, 0, 0)

                        numberOfPoints++
                    }
                    //обновление текста после ввода трёх точек
                    if (numberOfPoints == 3) {
                        textview.text = "Назначьте три точки конечного треугольника [4-5-6]"
                    }
                    //вычисление коэффициентов после ввода шести точек
                    if (numberOfPoints == 6) {
                        textview.text = "" //удаление текстовой подсказки
                        try {
                            //EditorFeatures.triangles(pointsX, pointsY)
                        } catch (e: OutOfMemoryError) {
                            Toast.makeText(applicationContext, "Недостаточно памяти для выполнения операции", Toast.LENGTH_SHORT).show()
                        }

                        imageview.setImageBitmap(tmpImage)
                        numberOfPoints++

                    }
                }
                return true
            }
        })
    }
}
