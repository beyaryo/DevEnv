package com.lynx.wind.dev.dialog

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.widget.*
import com.lynx.wind.dev.DevEnv
import com.lynx.wind.dev.intern.toDp

internal class DialogCustom(context: Context, listener: DialogListener, key: String) : Dialog(context) {

    private val DP10 by lazy { 10f.toDp(context).toInt() }
    private val DP15 by lazy { 15f.toDp(context).toInt() }
    private val SP17 = 17f

    private val data by lazy { DevEnv(context).getCustomSetting(key) }

    init {
        val container = RelativeLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            setPadding(DP10, DP10, DP10, DP10)
        }

        val lLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }

        val title = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.bottomMargin = DP15
            }

            setTextSize(TypedValue.COMPLEX_UNIT_SP, SP17)
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            text = key.capitalize()
        }

        val editText = EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            setPadding(DP15, DP10, DP15, DP10)

            inputType = InputType.TYPE_CLASS_TEXT
            maxLines = 1
            hint = "Enter Base URL"
        }

        val border = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }

        val button = Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                topMargin = DP10
                bottomMargin = DP10
            }

            text = "Save"

            setOnClickListener {
                if (editText.text.toString().isEmpty()) {
                    Toast.makeText(context, "Base URL can not empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                DevEnv(context).setCustom(key, editText.text.toString())
                listener.onDataChanged(key, editText.text.toString())
                dismiss()
            }
        }

        container.apply {
            addView(lLayout.apply {
                addView(title)
                addView(editText)
                addView(border)
                addView(button)
            })
        }

        setContentView(container)

        editText.setText(data as String)
    }
}