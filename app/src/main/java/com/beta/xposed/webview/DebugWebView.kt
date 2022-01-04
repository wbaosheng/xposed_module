package com.beta.xposed.webview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebView
import com.beta.xposed.monitor.PrivacyMonitor
import com.beta.xposed.monitor.method.ClassMethodGroup
import com.beta.xposed.monitor.method.HookMethodList
import com.beta.xposed.monitor.method.MethodWrapper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


object DebugWebView {
    const val tag = "HookWebview"

    val webViewMethodHandler = WebViewMethodHandler()

    fun start(methodList: HookMethodList) {
        Log.i(tag, "##### HookWebView")
        // 设置WebView可调试
        XposedHelpers.findAndHookConstructor(
            WebView::class.java,
            Context::class.java,
            AttributeSet::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    XposedHelpers.callStaticMethod(
                        WebView::class.java,
                        "setWebContentsDebuggingEnabled",
                        true
                    )
                }
            })
        hookMethod(methodList)
    }

    private fun hookMethod(methodList: HookMethodList) {
        for (methodWrapper in methodList.methodList) {
            registerMethod(methodWrapper)
        }
        for (classMethodGroup in methodList.absMethodList) {
            registerClass(classMethodGroup)
        }
    }

    private fun registerMethod(methodWrapper: MethodWrapper) {
        try {
            XposedHelpers.findAndHookMethod(
                methodWrapper.targetClass,
                methodWrapper.targetMethod,
                *methodWrapper.paramsWithDefaultHandler
            )
        } catch (error: NoSuchMethodError) {
            Log.wtf(PrivacyMonitor.TAG, "registeredMethod: NoSuchMethodError->" + error.message)
        }
    }

    private fun registerClass(classMethodGroup: ClassMethodGroup) {
        try {
            val clazz = Class.forName(classMethodGroup.targetClassName)
            val declareMethods = clazz.declaredMethods
            for (method in declareMethods) {
                if (classMethodGroup.methodGroup.contains(method.name)) {
                    XposedBridge.hookMethod(method, PrivacyMonitor.defaultMethodHandler)
                }
            }
        } catch (e: Exception) {
            Log.wtf(PrivacyMonitor.TAG, "registerClass Error-> " + e.message)
        }
    }
}