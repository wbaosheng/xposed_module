package com.beta.xposed.sp

import android.content.Context
import android.content.SharedPreferences
import com.beta.xposed.Const

object SPreference {
    fun pref(context: Context): SharedPreferences? {
        return try {
            // 7.0手机，如果是xposed框架版本小于93，Context.MODE_WORLD_READABLE会获取不到
            context.getSharedPreferences(Const.pref_config, Context.MODE_WORLD_READABLE)
        } catch (e: SecurityException) {
            context.getSharedPreferences(Const.pref_config, Context.MODE_PRIVATE)
        }
    }
}