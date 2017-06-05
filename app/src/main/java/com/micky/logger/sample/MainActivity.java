package com.micky.logger.sample;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.micky.logger.AndroidLogAdapter;
import com.micky.logger.CsvFormatStrategy;
import com.micky.logger.DiskLogAdapter;
import com.micky.logger.FormatStrategy;
import com.micky.logger.Logger;
import com.micky.logger.PrettyFormatStrategy;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.getInstance().init(this);
        setContentView(R.layout.activity_main);
        CrashHandler.getInstance().init(this);
        findViewById(R.id.btn_debug).setOnClickListener(this);
        findViewById(R.id.btn_info).setOnClickListener(this);
        findViewById(R.id.btn_error).setOnClickListener(this);

        initLogger();
    }

    private void initLogger() {
        Logger.clearLogAdapters();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("Console tag").build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return DEBUG;
            }
        });

        String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LoggerSample";
        FormatStrategy cvsFormatStrategy = CsvFormatStrategy.newBuilder()
                .tag("File tag")
                .logPath(logPath)
                .logFile("mylog")
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(cvsFormatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return DEBUG  || priority == Logger.ERROR;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_debug:
                Logger.d("This is debug message.");
                break;
            case R.id.btn_info:
                Logger.i("This is info message.");
                break;
            case R.id.btn_error:
                try {
                String a = "a";
                a.substring(0, 2);
                } catch (Exception e) {
                    for (int i = 0; i <= 100; i++)
                    Logger.t("AAA").e(e, "This is error message.");
                }
                break;
            default:
                break;
        }
    }
}
