package com.beta.xposed.dex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DexHook {
    static Method dexGetBytes;
    static Method getDex;

    public static void start(XC_LoadPackage.LoadPackageParam lpparam) {
        // XSharedPreferences xsp = new XSharedPreferences("com.ppma.appinfo", "User");
        // xsp.makeWorldReadable();
        // xsp.reload();
        // initReflect();
        // String packageName = xsp.getString("packageName", null);
        String packageName = "com.akax.haofangfa1";
        // XposedBridge.log("设定包名：" + packageName);
        if ((!lpparam.packageName.equals(packageName))) {
            XposedBridge.log("当前程序包名与设定不一致或者包名为空");
            return;
        }
        initReflect();
        XposedBridge.log("目标包名：" + lpparam.packageName);
        String str = "java.lang.ClassLoader";
        String str2 = "loadClass";
        XposedHelpers.findAndHookMethod(str, lpparam.classLoader, str2, String.class, Boolean.TYPE, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Class<?> cls = (Class<?>) param.getResult();
                if (cls == null) {
                    XposedBridge.log("cls == null");
                    return;
                }
                XposedBridge.log("当前类名：" +  cls.getName());
                byte[] bArr = (byte[]) dexGetBytes.invoke(getDex.invoke(cls, new Object[0]), new Object[0]);
                if (bArr == null) {
                    XposedBridge.log("数据为空：返回");
                    return;
                }
                XposedBridge.log("开始写数据");
                // String dex_path = "/data/data/" + packageName + "/" + packageName + "_" + bArr.length + ".dex";
                String dex_path = "/sdcard/Downloads/" + packageName + "_" + bArr.length + ".dex";
                XposedBridge.log(dex_path);
                File file = new File(dex_path);
                if (file.exists()) return;
                writeByte(bArr, file.getAbsolutePath());
            }
        });
    }

    public static void initReflect() {
        try {
            Class<?> dex = Class.forName("com.android.dex.Dex");
            dexGetBytes = dex.getDeclaredMethod("getBytes", new Class[0]);
            getDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex", new Class[0]);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            XposedBridge.log(e.getMessage());
            e.printStackTrace();
        }

    }

    public static void writeByte(byte[] bArr, String str) {
        try {
            OutputStream outputStream = new FileOutputStream(str);
            outputStream.write(bArr);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            XposedBridge.log("文件写出失败");
        }
    }
}
