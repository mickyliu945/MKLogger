package com.micky.logger.sample;

import android.content.Context;

import com.micky.logger.Logger;

/**
 * @Project iSphere
 * @Packate com.hy.imp.main.common.util
 *
 * @Description 全局异常捕获
 *
 * @Author Micky Liu
 * @Email liuhongwei@isphere.top
 * @Date 2016-03-09 14:40
 * @Company 北京华云合创科技有限公司成都分公司
 * @Copyright Copyright(C) 2016-2018
 * @Version 1.0.0
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    private Context mContext;

    private CrashHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.e(ex, "");
        mDefaultUEH.uncaughtException(thread, ex);
    }
}