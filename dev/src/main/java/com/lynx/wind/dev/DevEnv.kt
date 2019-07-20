package com.lynx.wind.dev

import android.content.Context
import android.util.Log

class DevEnv(private var context: Context?, private var appId: String?, private var isDebug: Boolean) {

    constructor(context: Context?) : this(context, null, false)

    private val session by lazy { Preference(context, Setting.PREFERENCE_KEY) }

    init {
        appId?.let {
            Setting.IS_DEBUG = isDebug
            Setting.PREFERENCE_KEY = "dev.$appId-preferences"
        }
    }

    fun setDefaultUrl(url: String): DevEnv {
        if (!session.getBoolean(Preference.KEY.IS_INITIALIZED) && isDebug()) {
            session.putString(Preference.KEY.DEFAULT_URL, url)
            session.putString(Preference.KEY.BASE_URL, url)
        } else {
            Setting.BASE_URL = url
        }
        return this
    }

    fun setCustomSetting(key: String, data: String): DevEnv {
        if (!session.getBoolean(Preference.KEY.IS_INITIALIZED) && isDebug()) {
            session.putString(key, data)
        } else {
            Setting.CUSTOM[key] = data
        }
        return this
    }

    fun setLogEnabled(isEnabled: Boolean): DevEnv {
        if (!session.getBoolean(Preference.KEY.IS_INITIALIZED) && isDebug()) {
            session.putBoolean(Preference.KEY.IS_LOG_ENABLED, isEnabled)
        } else {
            Setting.IS_LOG_ENABLED = isEnabled
        }
        return this
    }

    fun build() {
        session.putBoolean(Preference.KEY.IS_INITIALIZED, true)
    }

    fun getBaseUrl() =
        if (isDebug()) session.getString(Preference.KEY.BASE_URL)
        else Setting.BASE_URL

    fun getCustomSetting(key: String) =
        if (isDebug()) session.getString(key)
        else Setting.CUSTOM[key]

    fun isDebug() = Setting.IS_DEBUG

    fun log(msg: String) {
        if (internalIsLogEnabled()) Log.d(context?.packageName, msg)
    }

    internal fun internalSetBaseUrl(url: String) {
        session.putString(Preference.KEY.BASE_URL, url)
    }

    internal fun internalGetDefaultUrl() = session.getString(Preference.KEY.DEFAULT_URL)

    internal fun internalSetLogEnabled(isEnabled: Boolean) {
        session.putBoolean(Preference.KEY.IS_LOG_ENABLED, isEnabled)
    }

    internal fun internalIsLogEnabled() =
        if (isDebug()) session.getBoolean(Preference.KEY.IS_LOG_ENABLED)
        else Setting.IS_LOG_ENABLED

    internal fun internalGetCustoms() = session.getCustoms()

    internal fun internalSetCustom(key: String, data: String) {
        session.putString(key, data)
    }

    internal fun destroySession() = session.destroy()

    private object Setting {
        var IS_DEBUG = false
        var PREFERENCE_KEY = ""
        var BASE_URL = ""
        var IS_LOG_ENABLED = false
        var CUSTOM = HashMap<String, Any>()
    }

    private class Preference(context: Context?, prefKey: String) {
        private val session by lazy { context?.getSharedPreferences(prefKey, 0) }
        private val editor = session?.edit()

        object KEY {
            const val IS_INITIALIZED = "is-initialized"
            const val DEFAULT_URL = "default-url"
            const val BASE_URL = "base-url"
            const val IS_LOG_ENABLED = "is-log-enabled"
        }

        fun isContain(key: String) = session?.contains(key) ?: false

        fun putString(key: String, data: String) {
            editor?.putString(key, data)?.apply()
        }

        fun getString(key: String) = session?.getString(key, "") ?: ""

        fun putInt(key: String, data: Int) {
            editor?.putInt(key, data)?.apply()
        }

        fun getInt(key: String) = session?.getInt(key, 0) ?: 0

        fun putBoolean(key: String, data: Boolean) {
            editor?.putBoolean(key, data)?.apply()
        }

        fun getBoolean(key: String) = session?.getBoolean(key, false) ?: false

        fun destroy() {
            editor?.clear()?.commit()
        }

        fun getCustoms(): Map<String, *> = session?.all?.apply {
            this.remove(KEY.IS_INITIALIZED)
            this.remove(KEY.DEFAULT_URL)
            this.remove(KEY.BASE_URL)
            this.remove(KEY.IS_LOG_ENABLED)
        }!!
    }
}