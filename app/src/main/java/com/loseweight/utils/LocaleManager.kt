package com.loseweight.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.preference.PreferenceManager
import java.util.*


object LocaleManager {
    const val LANGUAGE_ENGLISH = "en"
    private const val LANGUAGE_KEY = "language_key"
    fun setLocale(c: Context): Context {
        return updateResources(c, getLanguage(c))
    }

    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(c, language)
        return updateResources(c, language)
    }

    fun getLanguage(c: Context?): String {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c!!)

        val lan =
            Locale.getDefault().language;

        return prefs.getString(Constant.PREF_LANGUAGE, lan)?:lan
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(c: Context, language: String) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
        // use commit() instead of apply(), because sometimes we kill the application process immediately
        // which will prevent apply() to finish
        prefs.edit().putString(LANGUAGE_KEY, language).commit()
    }

    private fun updateResources(context: Context, language: String): Context {
        var context: Context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.getResources()
        val config = Configuration(res.getConfiguration())
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.getDisplayMetrics())
        }
        return context
    }

    fun getLocale(res: Resources): Locale {
        val config: Configuration = res.getConfiguration()
        return if (Build.VERSION.SDK_INT >= 24) config.getLocales().get(0) else config.locale
    }
}