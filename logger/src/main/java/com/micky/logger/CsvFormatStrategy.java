package com.micky.logger;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CSV formatted file logging for Android.
 * Writes to CSV the following data:
 * epoch timestamp, ISO8601 timestamp (human-readable), log level, tag, log message.
 */
public class CsvFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " \r\n ";
    private static final String SEPARATOR = ",";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final LogStrategy logStrategy;
    private final String tag;

    private CsvFormatStrategy(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, String onceOnlyTag, String message) {
        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(Utils.logLevel(priority));

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
            message = "\"" + message + "\"";
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    private String formatTag(String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 5000 * 1024; // 500K averages to a 4000 lines per file

        private Date date;
        private SimpleDateFormat dateFormat;
        private LogStrategy logStrategy;
        private String tag = "MICKY_LOGGER";
        private String logPath;
        private String logFile;

        private Builder() {
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        public Builder logStrategy(LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public Builder tag(String tag) {
            if (!TextUtils.isEmpty(tag)) {
                this.tag = tag;
            }
            return this;
        }

        public Builder logPath(String path) {
            this.logPath = path;
            return this;
        }

        /**
         *
         * @param fileName 文件名（不包含后缀)
         * @return
         */
        public Builder logFile(String fileName) {
            this.logFile = fileName;
            return this;
        }

        public CsvFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
            }

            if (TextUtils.isEmpty(logPath)) {
                logPath = getDefaultLogPath();
            } else {
                File logPath = new File(this.logPath);
                if (logPath.exists() && !logPath.isDirectory()) {
                    this.logPath = getDefaultLogPath();
                }
            }
            if (TextUtils.isEmpty(logFile)) {
                logFile = "log";
            }
            if (logStrategy == null) {
                HandlerThread ht = new HandlerThread("AndroidFileLogger." + logPath);
                ht.start();
                Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(), logPath, logFile, MAX_BYTES);
                logStrategy = new DiskLogStrategy(handler);
            }
            return new CsvFormatStrategy(this);
        }

        private String getDefaultLogPath() {
            String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            return diskPath + File.separatorChar + "logger";
        }
    }
}
