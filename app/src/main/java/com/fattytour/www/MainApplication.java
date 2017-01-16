package com.fattytour.www;

import android.app.Application;
import android.content.Context;

/**
 * Created by Junjie on 6/01/2017.
 */

public class MainApplication extends Application {
    private static Application instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }
}
