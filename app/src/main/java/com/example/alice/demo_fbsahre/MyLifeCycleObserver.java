package com.example.alice.demo_fbsahre;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/**
 * Created by alice on 2018/11/26.
 */

public class MyLifeCycleObserver implements LifecycleObserver ,Callback{
    private static final String TAG="MyLifeCycleObserver";
    private String  currentPage ;
    private long  viewOn,viewOff;
    private Callback mCallback;

    public MyLifeCycleObserver(String currentPage) {
        this.currentPage = currentPage;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onActivityResume(){
        viewOn = System.currentTimeMillis();
        Log.d(TAG,"LifeCycle onActivityResume:"+currentPage);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onActivityPause(){
        Log.d(TAG,"LifeCycle onActivityPause:"+currentPage);
    }

    @Override
    public void GetNextPage(String NextPage) {
        Log.v(TAG,"GetNextPage:"+NextPage);
        viewOff = System.currentTimeMillis();
        Log.e("API","Form "+currentPage+" To "+NextPage+" during "+ String.valueOf((viewOff-viewOn)/1000));
    }

}
