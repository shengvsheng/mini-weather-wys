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


/**
 * Created by xiaosheng on 2017/12/6.
 */

public class GetDataService extends Service {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        Log.i("GetDataService", "GetDataService->Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GetDataService", "GetDataService->onStartCommand");
        MyTask myTask = new MyTask();
        myTask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String cityCode = sharedPreferences.getString("city_code", "101010100");
            String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
            String reponseStr = "";
            HttpURLConnection conn = null;
            try {
                Thread.sleep(3000);
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
                publishProgress();
            } catch (Exception e) {
                Log.i("NETWORK", "FAILDE");
                e.printStackTrace();
            }
            return reponseStr;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Intent intent = new Intent();//创建Intent对象
            intent.setAction("cn.pku.ui.service");
            intent.putExtra("data", response);
            sendBroadcast(intent);//发送广播
        }

    }

}
