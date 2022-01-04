package com.beta.xposed.webview

import android.webkit.ValueCallback
import android.webkit.WebView
import com.beta.xposed.monitor.method.ClassMethodGroup
import com.beta.xposed.monitor.method.HookMethodList
import com.beta.xposed.monitor.method.MethodWrapper
import java.util.*

class WebViewMethodList : HookMethodList {
    override fun getMethodList(): LinkedList<MethodWrapper> {
        val list = LinkedList<MethodWrapper>()
        list.add(
            MethodWrapper(
                WebView::class.java,
                "loadData",
                String::class.java,
                String::class.java,
                String::class.java
            ).setMethodHandler(DebugWebView.webViewMethodHandler)
        )
        list.add(
            MethodWrapper(
                WebView::class.java, "loadDataWithBaseURL",
                String::class.java,
                String::class.java,
                String::class.java,
                String::class.java,
                String::class.java
            ).setMethodHandler(DebugWebView.webViewMethodHandler)
        )
        list.add(
            MethodWrapper(
                WebView::class.java, "loadUrl",
                String::class.java
            ).setMethodHandler(DebugWebView.webViewMethodHandler)
        )
        list.add(
            MethodWrapper(
                WebView::class.java, "evaluateJavascript",
                String::class.java, ValueCallback::class.java
            ).setMethodHandler(DebugWebView.webViewMethodHandler)
        )
        return list
    }

    override fun getAbsMethodList(): LinkedList<ClassMethodGroup> {
        return LinkedList()
    }
}