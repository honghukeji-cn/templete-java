package com.honghukeji.hhkj.objs;

import com.honghukeji.hhkj.entity.Admin;

public class LocalAdmin {
    private final static ThreadLocal<Admin> threadLocal = new ThreadLocal<>();

    public static void set(Admin admin) {
        threadLocal.set(admin);
    }

    public static Admin get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
