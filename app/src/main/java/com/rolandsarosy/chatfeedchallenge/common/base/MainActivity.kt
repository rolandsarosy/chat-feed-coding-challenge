package com.rolandsarosy.chatfeedchallenge.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rolandsarosy.chatfeedchallenge.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
