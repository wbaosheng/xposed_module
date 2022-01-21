package com.beta.xposed.sp

import android.content.Context
import android.content.SharedPreferences
import com.beta.xposed.Const

object SPreference {
    fun pref(context: Context): SharedPreferences? {
        return try {
            context.getSharedPreferences(Const.pref_config, Context.MODE_PRIVATE)
        } catch (e: SecurityException) {
            null
        }
    }
}