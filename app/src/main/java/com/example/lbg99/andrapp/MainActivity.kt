package com.example.lbg99.andrapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun testtt(view: View) {
        val Testt = Toast.makeText(this, "PLS, HELP!!!", Toast.LENGTH_SHORT)
        Testt.show()
    }

    fun fix(view: View) {
        val cnt = Random()
        button1.text = cnt.nextInt().toString()
    }

    fun nni(view: View) {
        val ni = Intent(this, Main2Activity::class.java)
        val count = textView.text.toString()
        val test = Integer.parseInt(count)
        ni.putExtra(Main2Activity.TOTAL_COUNT,test)
        startActivity(ni)
    }
}
