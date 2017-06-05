package com.micky.logger;

public class DiskLogAdapter implements LogAdapter {

  private final FormatStrategy formatStrategy;

  public DiskLogAdapter() {
    formatStrategy = CsvFormatStrategy.newBuilder().build();
  }

  /**
   *
   * @param filePath 日志文件存储目录
   * @param fileName 日志文件名
   */
  public DiskLogAdapter(String filePath, String fileName) {
    formatStrategy = CsvFormatStrategy.newBuilder().logFilePath(filePath).logFileName(fileName).build();
  }

  /**
   *
   * @param tag      全局日志tag
   * @param filePath 日志文件存储目录
   * @param fileName 日志文件名
     */
  public DiskLogAdapter(String tag, String filePath, String fileName) {
    formatStrategy = CsvFormatStrategy.newBuilder().tag(tag).logFilePath(filePath).logFileName(fileName).build();
  }

  public DiskLogAdapter(FormatStrategy formatStrategy) {
    this.formatStrategy = formatStrategy;
  }

  @Override
  public boolean isLoggable(int priority, String tag) {
    return true;
  }

  @Override
  public void log(int priority, String tag, String message) {
    formatStrategy.log(priority, tag, message);
  }
}