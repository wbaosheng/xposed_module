package com.beta.xposed.webview

import android.util.Log
import com.beta.xposed.monitor.handle.MethodHandler

class WebViewMethodHandler : MethodHandler() {
    override fun beforeHookedMethod(param: MethodHookParam?) {
        param?.let { params ->
            Log.i(DebugWebView.tag, "# ${params.method.name} # ${params.args.joinToString()}")
        }
    }
}