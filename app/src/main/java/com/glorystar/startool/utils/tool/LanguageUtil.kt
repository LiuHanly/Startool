package com.glorystar.startool.utils.tool

import android.content.Context
import android.util.DisplayMetrics
import java.util.*

object LanguageUtil {
    fun changLang(context: Context?, Lan: Int) {
        var mylocale = when (Lan) {
            0 -> Locale("en", "US")
            1 -> Locale("zh", "CN")
            2 -> Locale("ja", "JP")
            3 -> Locale("fr", "FR")
            else -> null
        }
        val res = context!!.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(mylocale)
        res.updateConfiguration(conf, dm)
    }
}