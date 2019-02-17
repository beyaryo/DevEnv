package com.lynx.wind.dev

import android.content.Context

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
        if (!session.getBoolean(Preference.KEY.IS_INITIALIZED)) {
            session.putString(Preference.KEY.DEFAULT_URL, url)
            session.putString(Preference.KEY.BASE_URL, url)
        }
        return this
    }

    fun setCustomSetting(key: String, data: String): DevEnv{
        if (!session.getBoolean(Preference.KEY.IS_INITIALIZED)) {
            session.putString(key, data)
        }
        return this
    }

    fun build() = session.putBoolean(Preference.KEY.IS_INITIALIZED, true)

    fun getBaseUrl() = session.getString(Preference.KEY.BASE_URL)

    fun getCustomSetting(key: String) = session.getString(key)

    fun isDebug() = Setting.IS_DEBUG

    internal fun setBaseUrl(url: String) = session.putString(Preference.KEY.BASE_URL, url)

    internal fun getDefaultUrl() = session.getString(Preference.KEY.DEFAULT_URL)

    internal fun getCustoms() = session.getCustoms()

    internal fun setCustom(key: String, data: String) = session.putString(key, data)

    internal fun destroySession() = session.destroy()

    private object Setting {
        var IS_DEBUG = false
        var PREFERENCE_KEY = ""
    }

    private class Preference(context: Context?, prefKey: String) {
        private val session by lazy { context?.getSharedPreferences(prefKey, 0) }
        private val editor = session?.edit()

        object KEY {
            const val IS_INITIALIZED = "is-initialized"
            const val DEFAULT_URL = "default-url"
            const val BASE_URL = "base_url"
        }

        fun isContain(key: String): Boolean {
            return session?.contains(key) ?: false
        }

        fun putString(key: String, data: String) {
            editor?.putString(key, data)?.apply()
        }

        fun getString(key: String): String {
            return session?.getString(key, "") ?: ""
        }

        fun putInt(key: String, data: Int) {
            editor?.putInt(key, data)?.apply()
        }

        fun getInt(key: String): Int {
            return session?.getInt(key, 0) ?: 0
        }

        fun putBoolean(key: String, data: Boolean) {
            editor?.putBoolean(key, data)?.apply()
        }

        fun getBoolean(key: String): Boolean {
            return session?.getBoolean(key, false) ?: false
        }

        fun destroy() {
            editor?.clear()?.commit()
        }

        fun getCustoms(): Map<String, *>{
            return session?.all?.apply {
                this.remove(KEY.IS_INITIALIZED)
                this.remove(KEY.DEFAULT_URL)
                this.remove(KEY.BASE_URL)
            }!!
        }
    }
}