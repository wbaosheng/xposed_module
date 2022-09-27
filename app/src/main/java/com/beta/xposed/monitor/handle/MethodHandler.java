package com.beta.xposed.monitor.handle;

import android.os.Process;
import android.util.Log;

import com.beta.xposed.monitor.PrivacyMonitor;

import de.robv.android.xposed.XC_MethodHook;

public class MethodHandler extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        Log.i(PrivacyMonitor.TAG,  PrivacyMonitor.processName + "**检测到风险调用函数-> " + param.method.getDeclaringClass().getName() + "#" + param.method.getName());
        Log.d(PrivacyMonitor.TAG, getMethodStack());
    }

    private static String getMethodStack() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean skip = true;
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement temp : stackTraceElements) {
            String line = temp.toString();
            boolean isEpic = line.startsWith("EdHooker_") || line.startsWith("LSPHooker_") ;

            if (!skip && !isEpic) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }

            if (isEpic && skip) {
                skip = false;
            }
        }

        return stringBuilder.toString();
    }
}
