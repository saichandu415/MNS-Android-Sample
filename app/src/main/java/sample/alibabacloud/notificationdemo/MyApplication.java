package sample.alibabacloud.notificationdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

/**
 * Created by Sarath Chandra
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this,ReceiveService.class));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
