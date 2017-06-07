package com.micky.logger.sample;

import android.content.Context;

import com.micky.logger.Logger;

/**
 * @Project LoggerSample
 *
 * @Description 全局异常捕获
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2016-6-06 14:40
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