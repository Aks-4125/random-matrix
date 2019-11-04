package com.example.randommatrix

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {


    companion object {
        private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())

    }

    @LayoutRes
    abstract fun layoutId(): Int

}