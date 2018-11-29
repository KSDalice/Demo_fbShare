package com.example.alice.demo_fbsahre;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by alice on 2018/11/23.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if(activity.getClass()==MainActivity.class){
                Log.v(TAG,"MainActivityCreated.");
            }else if(activity.getClass()==GoogleLogin.class) {
                Log.v(TAG,"GoogleLoginCreated.");
            }else if(activity.getClass()==FbLogin.class){
                Log.v(TAG,"FbLoginCreated.");
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityStarted.");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityResumed.");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityPaused.");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityStopped.");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityStoppedonActivitySaveInstanceState.");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, activity.getClass().getSimpleName()+"onActivityDestroyed.");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onTerminate();
    }
}
