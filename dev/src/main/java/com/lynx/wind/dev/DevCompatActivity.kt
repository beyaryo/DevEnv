package com.lynx.wind.dev

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.lynx.wind.dev.intern.DevSettingActivity

open class DevCompatActivity : AppCompatActivity() {

    private val shakeDialog by lazy {
        AlertDialog
            .Builder(this)
            .setMessage("Do you want to open development setting page?")
            .setPositiveButton("Yes") { dialog, _ ->
                startActivity(Intent(this@DevCompatActivity, DevSettingActivity::class.java))
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
    }

    private val detector by lazy {
        ShakeDetector().apply {
            shakeNumber = 2
            threshold = 1f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detector.create(this, object : ShakeListener {
            override fun onShake() {
                shakeDialog.show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (DevEnv(this).isDebug()) detector.start()
    }

    override fun onPause() {
        super.onPause()
        detector.stop()
    }
}