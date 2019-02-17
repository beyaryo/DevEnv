package com.lynx.wind.dev.intern

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lynx.wind.dev.DevEnv
import com.lynx.wind.dev.R
import com.lynx.wind.dev.dialog.DialogBaseUrl
import com.lynx.wind.dev.dialog.DialogListener
import kotlinx.android.synthetic.main.activity_dev_setting.*


internal class DevSettingActivity : AppCompatActivity(), DialogListener {

    private val env by lazy { DevEnv(this) }
    private val custom by lazy { env.getCustoms() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupView()
        setBaseUrlText()
    }

    private fun setupView() {
        action_clear_cache.setOnClickListener { showAlertCache() }
        action_base_url.setOnClickListener {
            DialogBaseUrl(this, this).show()
        }

        if (custom.isNotEmpty()) {
            txt_custom.visibility = View.VISIBLE

            for (key in custom.keys) {
                val lLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    val tv = TypedValue()
                    this@DevSettingActivity.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground,
                        TypedValue(),
                        true
                    )
                    setBackgroundResource(tv.resourceId)
                    isClickable = true
                    isFocusable = true
                    orientation = LinearLayout.HORIZONTAL
                }

                val tvLabel = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        weight = 1f
                    }
                    setPadding(10.toDp(), 15.toDp(), 10.toDp(), 15.toDp())

                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                    setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.black))
                    text = key
                }

                val tvValue = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        weight = 1f
                    }
                    setPadding(10.toDp(), 15.toDp(), 10.toDp(), 15.toDp())
                    ellipsize = TextUtils.TruncateAt.END
                    maxLines = 1

                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
                    setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.holo_blue_light))
                    textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
                    text = custom[key] as String
                }

                val v = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.darker_gray))
                }

                container_custom.also {
                    it.addView(lLayout.apply {
                        addView(tvLabel)
                        addView(tvValue)
                    })
                    it.addView(v)
                }
            }
        }
    }

    private fun setBaseUrlText() {
        action_base_url.text = DevEnv(this).getBaseUrl()
    }

    private fun showAlertCache() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure to wipe all application data? Application will be closed after proses.")
            .setPositiveButton("Yes") { dialog, _ ->
                DevEnv(this@DevSettingActivity).destroySession()
                cacheDir.deleteRecursively()
                (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .setCancelable(true).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDataChanged(tag: String, data: Any?) {
        when (tag) {
            DialogBaseUrl.TAG -> setBaseUrlText()
        }
    }

    private fun Int.toDp() = Math.round(this * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}