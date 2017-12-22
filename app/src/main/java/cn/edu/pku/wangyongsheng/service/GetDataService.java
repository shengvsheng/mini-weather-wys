package cn.edu.pku.wangyongsheng.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.wangyongsheng.miniweather.MainActivity;


/**
 * 用来实时更新天气信息的数据
 * 通过Handler通知MainActivity更新UI
 */

public class GetDataService extends Service {
    private SharedPreferences sharedPreferences;
    private boolean isRunning = true;
    private Thread mThread;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        Log.i("GetDataService", "GetDataService->Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GetDataService", "GetDataService->onStartCommand");
        //启动一个子线程每5秒循环获取天气数据并通知主Activity更新数据
        mThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        String cityCode = sharedPreferences.getString("city_code", "101010100");
                        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
                        String reponseStr = "";
                        HttpURLConnection conn = null;
                        URL url = new URL(address);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(8000);
                        conn.setReadTimeout(8000);
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder reponse = new StringBuilder();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            reponse.append(str);
                        }
                        reponseStr = reponse.toString();
                        Message message = MainActivity.mUpdateHandler.obtainMessage();
                        message.what = MainActivity.UPDATE_DATA;
                        message.obj = reponseStr;
                        MainActivity.mUpdateHandler.sendMessage(message);
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        Log.i("NETWORK", "FAILDE");
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //当service停止后，取消子线程的循环
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("GetDataService", "GetDataService->onDestroy");
        isRunning = false;
    }


}
