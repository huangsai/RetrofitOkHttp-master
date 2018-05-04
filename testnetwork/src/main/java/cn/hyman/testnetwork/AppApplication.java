package cn.hyman.testnetwork;

import android.app.Application;

/**
 * Created by hyman on 2018/5/4.
 */
public class AppApplication extends Application {

    private AppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        HttpRequestManager.initialize(instance);
    }
}
