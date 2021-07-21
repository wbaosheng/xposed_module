package com.beta.xposed;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage 开始执行");
        if (lpparam.packageName.equals("com.beta.browser")) {
            XposedBridge.log("开始执行 com.beta.browser 的hook");
            hookContextImplStartService(lpparam);
            hookApplicationThreadScheduleCreateService(lpparam);
            hookActivityThreadHandleCreateService(lpparam);
        }
        if (lpparam.packageName.equals("com.baidu.homework")) {
            hookPrivateInfo(lpparam);
        }

    }

    private void hookPrivateInfo(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        final Class<?> telephoneManager = lpparam.classLoader.loadClass("android.telephony.TelephonyManager");
        XposedHelpers.findAndHookMethod(telephoneManager, "getDeviceId", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log(">>> beforeHookedMethod " + telephoneManager + "::getDeviceId");
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    XposedBridge.log("    >>>>>>" + element.toString());
                }
            }
        });
    }

    @SuppressLint("PrivateApi")
    private void hookActivityThreadHandleCreateService(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class ActivityThread = lpparam.classLoader.loadClass("android.app.ActivityThread");
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
