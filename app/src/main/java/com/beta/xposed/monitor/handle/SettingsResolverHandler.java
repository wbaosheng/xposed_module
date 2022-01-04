package com.beta.xposed.monitor.handle;

import android.util.Log;

import com.beta.xposed.monitor.PrivacyMonitor;

import de.robv.android.xposed.XC_MethodHook;

public class SettingsResolverHandler extends MethodHandler {

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Log.i(PrivacyMonitor.TAG, PrivacyMonitor.processName + "**检测到风险Settings查询: " + param.args[1].toString());
        super.beforeHookedMethod(param);
    }
}
