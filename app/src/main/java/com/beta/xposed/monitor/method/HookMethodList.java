package com.beta.xposed.monitor.method;

import java.util.LinkedList;

public interface HookMethodList {
    LinkedList<MethodWrapper> getMethodList();

    LinkedList<ClassMethodGroup> getAbsMethodList();
}
