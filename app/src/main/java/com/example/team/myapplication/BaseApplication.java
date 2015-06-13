package com.example.team.myapplication;

import android.app.Activity;
import android.app.Application;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by coco on 2015/6/14.
 */
public class BaseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LoginState.setPage(1);
                LoginState.fresh=true;
            }

            @Override
            public void onActivityDestroyed(Activity arg0) {
            }
        });
    }
}
