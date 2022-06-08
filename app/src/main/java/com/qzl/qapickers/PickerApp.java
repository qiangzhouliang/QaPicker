package com.qzl.qapickers;

import android.app.Application;

import com.qzl.qapickers.common.AppConfig;
import com.qzl.qapickers.util.LogUtils;


/**
 * @author matt
 * blog: addapp.cn
 */
public class PickerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setIsDebug(BuildConfig.DEBUG);
        if (!LogUtils.isDebug()) {
            android.util.Log.d(AppConfig.DEBUG_TAG, "logcat is disabled");
        }
    }

}
