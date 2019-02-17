package com.lynx.wind.devenvsample

import android.annotation.SuppressLint
import android.os.Bundle
import com.lynx.wind.dev.DevCompatActivity
import com.lynx.wind.dev.DevEnv
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DevCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.text = "${DevEnv(this).getBaseUrl()} ${DevEnv(this).getCustomSetting("image")}"
    }
}
