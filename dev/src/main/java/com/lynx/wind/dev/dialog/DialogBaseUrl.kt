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
import kotlin.properties.Delegates

internal class DialogBaseUrl(context: Context, listener: DialogListener) : Dialog(context) {

    companion object {
        const val TAG = "dialog-base-url"
    }

    private var baseUrl by Delegates.observable("") { _, _, newValue ->
        checkBox.isChecked = newValue == defaultUrl
    }
    private val defaultUrl by lazy { DevEnv(context).internalGetDefaultUrl() }
    private var checkBox: CheckBox

    private val DP5 by lazy { 5f.toDp(context).toInt() }
    private val DP10 by lazy { 10f.toDp(context).toInt() }
    private val DP15 by lazy { 15f.toDp(context).toInt() }
    private val SP15 = 15f
    private val SP17 = 17f

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
            text = "Base URL"
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

        checkBox = CheckBox(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, DP5, 0, DP5)

            setTextSize(TypedValue.COMPLEX_UNIT_SP, SP15)
            text = "Use default URL"

            setOnCheckedChangeListener { _, isChecked ->
                editText.isEnabled = !isChecked

                if (isChecked) editText.setText(defaultUrl)
            }
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

                baseUrl = editText.text.toString()

                DevEnv(context).internalSetBaseUrl(baseUrl)
                listener.onDataChanged(TAG, baseUrl)
                dismiss()
            }
        }

        container.apply {
            addView(lLayout.apply {
                addView(title)
                addView(editText)
                addView(border)
                addView(checkBox)
                addView(button)
            })
        }

        setContentView(container)

        baseUrl = DevEnv(context).getBaseUrl()
        editText.setText(baseUrl)
    }
}