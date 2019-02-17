package com.lynx.wind.dev.intern

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.lynx.wind.dev.DevEnv
import com.lynx.wind.dev.dialog.DialogBaseUrl
import com.lynx.wind.dev.dialog.DialogCustom
import com.lynx.wind.dev.dialog.DialogListener


internal class DevSettingActivity : AppCompatActivity(), DialogListener {

    private val container by lazy { LinearLayout(this) }
    private val env by lazy { DevEnv(this) }
    private val custom by lazy { env.getCustoms() }

    private val txts = HashMap<String, TextView>()

    private val SP15 = 15f
    private val SP17 = 17f
    private val DP10 by lazy { 10f.toDp(this).toInt() }
    private val DP15 by lazy { 15f.toDp(this).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        buildView()

        setBaseUrlText()
    }

    private fun buildView() {
        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        container.apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, DP10)
        }

        buildNetworkSection()
        buildCustomSection()
        buildMiscSection()

        scroll.addView(container)
        setContentView(scroll)
    }

    private fun buildNetworkSection() {
        container.also {
            it.addView(textTitle("Network"))
            it.addView(containerLayout().apply {
                addView(textLabel("Base URL"))
                addView(textValue {
                    DialogBaseUrl(this@DevSettingActivity, this@DevSettingActivity).show()
                }.apply {
                    txts["base-url"] = this
                })
            })
            it.addView(borderBottom())
        }

        setBaseUrlText()
    }

    private fun buildCustomSection() {
        if (custom.isNotEmpty()) {
            container.also {
                it.addView(textTitle("Custom Setting"))

                for (key in custom.keys) {
                    it.addView(containerLayout().apply {
                        addView(textLabel(key))
                        addView(textValue {
                            DialogCustom(this@DevSettingActivity, this@DevSettingActivity, key).show()
                        }.apply {
                            txts[key] = this
                            text = custom[key] as String
                        })
                    })
                }
                it.addView(borderBottom())
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildMiscSection() {
        container.also {
            it.addView(textTitle("Misc"))
            it.addView(TextView(this).apply {
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

                setPadding(DP10, DP15, DP10, DP15)
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1

                setTextSize(TypedValue.COMPLEX_UNIT_SP, SP17)
                setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.holo_red_light))
                text = "Clear app cache and data"

                setOnClickListener { showAlertCache() }
            })
            it.addView(borderBottom())
        }
    }

    private fun setBaseUrlText() {
        txts["base-url"]?.text = DevEnv(this).getBaseUrl()
    }

    private fun setCustomText(key: String){
        txts[key]?.text = DevEnv(this).getCustomSetting(key)
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
            else -> setCustomText(tag)
        }
    }

    private fun textTitle(title: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.topMargin = DP10
            }

            setPadding(DP10, DP10, DP10, DP10)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, SP15)
            setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.holo_blue_dark))
            text = title.capitalize()
        }
    }

    private fun textLabel(title: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            setPadding(DP10, DP15, DP10, DP15)

            setTextSize(TypedValue.COMPLEX_UNIT_SP, SP17)
            setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.black))
            text = title.capitalize()
        }
    }

    private fun textValue(action: () -> Unit): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                weight = 1f
            }
            setPadding(DP10, DP15, DP10, DP15)
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1

            setTextSize(TypedValue.COMPLEX_UNIT_SP, SP17)
            setTextColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.darker_gray))
            textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END

            setOnClickListener { action() }
        }
    }

    private fun containerLayout(): LinearLayout {
        return LinearLayout(this).apply {
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
    }

    private fun borderBottom(): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            setBackgroundColor(ContextCompat.getColor(this@DevSettingActivity, android.R.color.darker_gray))
        }
    }
}