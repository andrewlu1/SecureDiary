package cn.andrewlu.app.securediary;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

import cn.andrewlu.app.securediary.utils.LocationUtil;

/**
 * Created by andrewlu on 2015/11/9.
 */
public class MyApp extends Application {
    private static MyApp _instance = null;

    public void onCreate() {
        super.onCreate();
        _instance = this;
        LocationUtil.init(this);
        CrashReport.initCrashReport(this, "900014104", true);
    }

    public static MyApp instance() {
        return _instance;
    }
}
