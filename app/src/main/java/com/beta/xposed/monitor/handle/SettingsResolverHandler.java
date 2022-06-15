package com.beta.xposed.monitor.handle;

import android.util.Log;

import com.beta.xposed.monitor.PrivacyMonitor;

import de.robv.android.xposed.XC_MethodHook;

public class SettingsResolverHandler extends MethodHandler {

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        String str = param.args[1].toString();
        Log.i(PrivacyMonitor.TAG + "-Settings", PrivacyMonitor.processName + "**检测到风险Settings查询: " + str);
        if ("android_id".equalsIgnoreCase(str)) {
            super.beforeHookedMethod(param);
        }
    }
}
