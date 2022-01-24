package com.beta.xposed;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import com.beta.xposed.monitor.PrivacyMonitor;
import com.beta.xposed.monitor.method.NormalMethodList;
import com.beta.xposed.webview.DebugWebView;
import com.beta.xposed.webview.WebViewMethodList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    private static XSharedPreferences getPref(String path) {
        XSharedPreferences pref;
        try {
            pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, path);
            if (pref.getFile() == null || !pref.getFile().canRead()) {
                pref = null;
            }
        } catch (Exception e) {
            pref = null;
            XposedBridge.log("pref: " + e.getMessage());
        }
        return pref;
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {

    }

    private String getProp(String key) {
        String value = "";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            method.setAccessible(true);
            value = (String) method.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 获取包名
        String pkgName = getProp("com.beta.pkg");
        XSharedPreferences prefs = getPref(Const.pref_config);
        if (prefs != null) {
            pkgName = prefs.getString(Const.pref_key_pkgname, pkgName);
        }

        // 是否debugWebview
        String webview = getProp("com.beta.webview");
        boolean debugWv = "1".equals(webview);
        if (prefs != null) {
            debugWv = prefs.getBoolean(Const.pref_key_debug_wv, debugWv);
        }

        XposedBridge.log("配置要hook的包名: >>> " + pkgName);
        if (lpparam.packageName.equals(pkgName)) {
            XposedBridge.log("配置要hook的包名: " + pkgName);
            // DexHook.start(lpparam);
            PrivacyMonitor.start(lpparam.processName, new NormalMethodList());

            if (debugWv) {
                DebugWebView.INSTANCE.start(new WebViewMethodList());
            }
        }
        // if (lpparam.packageName.equals("com.beta.xposed")
        // ) {
        //     DexHook.start(lpparam);
        //     PrivacyMonitor.start(lpparam.processName, new NormalMethodList());
        //     // hookSecureGetString(lpparam);
        //     // hookNetworkInterfaceGetHardwareAddress(lpparam);
        //     // hookWifiInfo(lpparam);
        //     // hookWifiInfoGetMacAddress(lpparam);
        //     // hookWifiInfoGetSSID(lpparam);
        //     // hookWifiInfoGetIpAddress(lpparam);
        //     // hookInetAddressGetHostAddress(lpparam);
        //     // hooktest(lpparam);
        // }

    }

    private void hooktest(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> clazz = lpparam.classLoader.loadClass("com.qiyukf.unicorn.api.Unicorn");
        XposedHelpers.findAndHookMethod(clazz, "initSdk", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod: " + clazz.getName());
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookSecureGetString(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> secure = lpparam.classLoader.loadClass("android.provider.Settings$Secure");
        XposedHelpers.findAndHookMethod(secure, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if ("android_id".equals(param.args[1])) {
                    XposedBridge.log(">>> beforeHookedMethod " + secure + " " + param.args[1]);
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    for (StackTraceElement element : stackTraceElements) {
                        XposedBridge.log("    >>>>>>" + element.toString());
                    }
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookWifiInfo(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> wifiInfo = lpparam.classLoader.loadClass("android.net.wifi.WifiManager");
        XposedHelpers.findAndHookMethod(wifiInfo, "getConnectionInfo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + wifiInfo + "::getConnectionInfo");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookWifiInfoGetMacAddress(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> wifiInfo = lpparam.classLoader.loadClass("android.net.wifi.WifiInfo");
        XposedHelpers.findAndHookMethod(wifiInfo, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + wifiInfo + "::getMacAddress");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookWifiInfoGetSSID(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> wifiInfo = lpparam.classLoader.loadClass("android.net.wifi.WifiInfo");
        XposedHelpers.findAndHookMethod(wifiInfo, "getSSID", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + wifiInfo + "::getSSID");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookWifiInfoGetIpAddress(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> wifiInfo = lpparam.classLoader.loadClass("android.net.wifi.WifiInfo");
        XposedHelpers.findAndHookMethod(wifiInfo, "getIpAddress", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + wifiInfo + "::getIpAddress");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookNetworkInterfaceGetHardwareAddress(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> netWorkInterface = lpparam.classLoader.loadClass("java.net.NetworkInterface");
        XposedHelpers.findAndHookMethod(netWorkInterface, "getHardwareAddress", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + netWorkInterface + "::getHardwareAddress");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookInetAddressGetHostAddress(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> inetAddress = lpparam.classLoader.loadClass("java.net.InetAddress");
        XposedHelpers.findAndHookMethod(inetAddress, "getHostAddress", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + inetAddress + "::getHostAddress");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookActivityThreadHandleCreateService(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> ActivityThread = lpparam.classLoader.loadClass("android.app.ActivityThread");
        Class CreateServiceData = lpparam.classLoader.loadClass("android.app.ActivityThread$CreateServiceData");
        final Field info_field = CreateServiceData.getDeclaredField("info");
        info_field.setAccessible(true);
        final Field token_filed = CreateServiceData.getDeclaredField("token");
        token_filed.setAccessible(true);
        XposedHelpers.findAndHookMethod(ActivityThread, "handleCreateService",
                CreateServiceData,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object data = param.args[0];
                        ServiceInfo serviceInfo = (ServiceInfo) info_field.get(data);
                        IBinder binder = (IBinder) token_filed.get(data);
                        XposedBridge.log("beforeHookedMethod " + data.toString());
                        if (serviceInfo.name.contains("DownloadService")
                                || serviceInfo.name.contains("DownloadNotificationService")) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (StackTraceElement element : stackTraceElements) {
                                XposedBridge.log("    >>>>>>" + element.toString());
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
    }

    @SuppressLint("PrivateApi")
    private void hookApplicationThreadScheduleCreateService(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class ApplicationThread = lpparam.classLoader.loadClass("android.app.ActivityThread$ApplicationThread");
        Class CompatibilityInfo = lpparam.classLoader.loadClass("android.content.res.CompatibilityInfo");
        XposedHelpers.findAndHookMethod(ApplicationThread, "scheduleCreateService",
                IBinder.class,
                ServiceInfo.class,
                CompatibilityInfo,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        final int callingPid = Binder.getCallingPid();
                        final int callingUid = Binder.getCallingUid();
                        IBinder token = (IBinder) param.args[0];
                        ServiceInfo serviceInfo = (ServiceInfo) param.args[1];
                        XposedBridge.log("beforeHookedMethod " + token
                                + " serviceInfo:" + serviceInfo + " process:" + serviceInfo.processName + " callingPid:" + callingPid + " callingUid:" + callingUid);
                        if (serviceInfo.name.contains("DownloadService")
                                || serviceInfo.name.contains("DownloadNotificationService")) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (StackTraceElement element : stackTraceElements) {
                                XposedBridge.log("    >>>>>>" + element.toString());
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
    }

    @SuppressLint("PrivateApi")
    private void hookContextImplStartService(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class ApplicationThread = lpparam.classLoader.loadClass("android.app.ContextImpl");
        XposedHelpers.findAndHookMethod(ApplicationThread, "startService",
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[0];
                        XposedBridge.log("beforeHookMethod " + intent);
                        ComponentName componentName = intent.getComponent();
                        if (componentName == null) {
                            return;
                        }
                        if (componentName.getClassName().contains("DownloadService")
                                || componentName.getClassName().contains("DownloadNotificationService")) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (StackTraceElement element : stackTraceElements) {
                                XposedBridge.log("    >>>>>>" + element.toString());
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
    }
}
