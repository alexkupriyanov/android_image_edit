package com.example.lbg99.andrapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

import android.R.id.button1
import android.R.id.button2
import com.example.lbg99.andrapp.R
import com.example.lbg99.andrapp.R.layout.activity_main

class MainActivity : AppCompatActivity() {
    internal var iv: ImageView
    internal var btn1: Button
    internal var btn2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv = findViewById<View>(R.id.imageView) as ImageView
        btn1 = findViewById<View>(R.id.button1) as Button
        btn2 = findViewById<View>(R.id.button2) as Button

        btn1.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.clockwise)
            iv.startAnimation(anim)
        }//btn1

        btn2.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(applicationContext, R.anim.anticlockwise)
            iv.startAnimation(anim)
        }//btn2

    }//onCfreate
}
