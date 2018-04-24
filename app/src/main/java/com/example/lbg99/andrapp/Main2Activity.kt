package com.example.lbg99.andrapp

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class Main2Activity : AppCompatActivity() {

    companion object {
        const val TOTAL_COUNT = "total_count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        newRand()
    }

    @SuppressLint("StringFormatInvalid", "SetTextI18n")
    fun newRand()
    {
        val count = intent.getIntExtra(TOTAL_COUNT,0)
        val rand = Random()
        var randInt = 0
        if(count > 0) {
            randInt = rand.nextInt(count + 1)
        }
        textView3.text = getString(R.string.rand_text,count)
        textView4.text = Integer.toString(randInt)
    }
}
