package com.example.iso8583.util;

import android.util.Log;

import java.util.Locale;

/**
 * 日志输出控制工具
 * 所有日志输出应根据此工具
 * @author a
 */
public class LogUtils {

    private static String TAG_EXT = "(LOG_EXT) ";
    private static String TAG_I = "(LOG_I) ";
    private static String TAG_D = "(LOG_D) ";
    private static String TAG_E = "(LOG_E) ";
    private static String TAG_W = "(LOG_W) ";
    private static String TAG_P = "(LOG_P) ";
    private static String TAG_G = "(LOG_G) ";
    private static final boolean isDeBug = true;
    private static Throwable t = new Throwable();

    /**
     * 外部回调及其他用处日志输出，可由 FinalConfigs.isDeBug 控制是否输出
     * @param format
     * @param args
     */
    public static void ext(String format, Object... args) {
        if(Constants.isDeBug) {
            String msg = buildMessage(format, args);
            Log.i(TAG_EXT, msg);
        }
    }

    /**
     * 系统常规日志显示输出
     * @param format
     * @param args
     */
    public static void i(String format, Object... args) {
        if(isDeBug) {
            String msg = buildMessage(format, args);
            Log.i(TAG_I, msg);
        }
    }

    /**
     * 系统调试日志显示输出
     * @param format
     * @param args
     */
    public static void d(String format, Object... args) {
        if(isDeBug) {
            String msg = buildMessage(format, args);
            Log.d(TAG_D, msg);
        }
    }

    /**
     * 系统错误日志显示输出
     * @param format
     * @param args
     */
    public static void e(String format, Object... args) {
        if(isDeBug) {
            String msg = buildMessage(format, args);
            Log.e(TAG_E, msg);
        }
    }

    public static void w(String format, Object... args) {
        if(isDeBug) {
            String msg = buildMessage(format, args);
            Log.w(TAG_W, msg);
        }
    }

    /**
     * 常规的数据如域解析日志输出，使用System.out.print看起来容易分析数据
     * @param obj
     */
    public static void p(Object obj) {
        if(isDeBug) {
            System.out.println(TAG_P + obj);
        }
    }

    /**
     * 系统错误日志显示输出
     * @param format
     * @param args
     */
    public static void g(String format, Object... args) {
        if(isDeBug) {
            String msg = buildMessage(format, args);
            Log.e(TAG_G, msg);
        }
    }

    /**
     * 日志输出
     * @param format
     * @param args
     * @return
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.US, format, args);

        StackTraceElement[] trace = t.fillInStackTrace().getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            StackTraceElement traceElement = trace[i];
            if(traceElement.getClassName().contains(Constants.PACKAGE_NAME)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                caller = callingClass + "." + trace[i].getMethodName() +
                        "[第" + trace[i].getLineNumber() + "行]";
                break;
            } else {
                //非法hook
                System.out.println("非法hook");
            }
        }
        return String.format(Locale.US, "[%s] path:%s", msg, caller);
    }
}
