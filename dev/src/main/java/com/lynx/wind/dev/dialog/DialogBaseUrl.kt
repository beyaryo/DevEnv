package com.lynx.wind.dev.dialog

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.lynx.wind.dev.DevEnv
import com.lynx.wind.dev.R
import kotlinx.android.synthetic.main.dialog_base_url.*
import kotlin.properties.Delegates

internal class DialogBaseUrl(context: Context, listener: DialogListener) : Dialog(context) {

    companion object {
        const val TAG = "dialog-base-url"
    }

    private var baseUrl by Delegates.observable("") { _, _, newValue ->
        checkbox.isChecked = newValue == defaultUrl
    }
    private val defaultUrl by lazy { DevEnv(context).getDefaultUrl() }

    init {
        setContentView(R.layout.dialog_base_url)

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            edit.isEnabled = !isChecked

            if (isChecked) edit.setText(defaultUrl)
        }

        baseUrl = DevEnv(context).getBaseUrl()

        edit.setText(baseUrl)

        button.setOnClickListener {
            if (edit.text.toString().isEmpty()) {
                Toast.makeText(context, "Base URL can not empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            baseUrl = edit.text.toString()

            DevEnv(context).setBaseUrl(baseUrl)
            listener.onDataChanged(TAG, baseUrl)
            dismiss()
        }
    }
}