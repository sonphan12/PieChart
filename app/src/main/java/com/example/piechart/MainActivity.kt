package com.example.piechart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pieChart.setRatios(
            listOf(
                0.1f,
                0.1f,
                0.3f,
                0.25f,
                0.25f
            )
        )
    }
}
