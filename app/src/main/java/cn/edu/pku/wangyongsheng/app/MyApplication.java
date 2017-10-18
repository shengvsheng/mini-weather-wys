package cn.edu.pku.wangyongsheng.app;

import android.app.Application;
import android.util.Log;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class MyApplication extends Application {
    private static final String APP_TAG="myApp";
    private static MyApplication myApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(APP_TAG,"MyApplication->Create");
        myApplication=this;
    }
    public static MyApplication getInstance(){
        return myApplication;
    }
}
