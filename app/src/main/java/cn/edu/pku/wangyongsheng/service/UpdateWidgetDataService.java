package cn.edu.pku.wangyongsheng.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import cn.edu.pku.wangyongsheng.bean.TodayWeather;
import cn.edu.pku.wangyongsheng.miniweather.R;
import cn.edu.pku.wangyongsheng.widget.MyAppWidget;
/**
 * 用来实时更新小插件Widget的天气数据
 * 通过RomoteViews获取小插件的布局
 * 通过Handler通知更新RomoteViews里的控件内容，使用AppWidgetManager控制更新小插件的天气数据
 */

public class UpdateWidgetDataService extends Service {
    private SharedPreferences sharedPreferences;
    private static final int UPDATE_MESSAGE  = 1000;
    private UpdateHandler updateHandler;
    private Thread mThread;
    boolean isRunning=true;
    //服务启动，执行onCreate方法，对一些类初始化
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        updateHandler = new UpdateHandler();
        Log.i("WidgetService", "GetDataService->Created");
    }
    //执行后启动一个子线程没10秒循环执行更新widget插件天气信息
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("WidgetService", "GetDataService->onStartCommand");
        mThread=new Thread() {
            @Override
            public void run() {
                String cityCode = sharedPreferences.getString("city_code", "101010100");
                String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
                while (isRunning) {
                    try {
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
                        Message message = updateHandler.obtainMessage();
                        message.what = UPDATE_MESSAGE;
                        message.obj=reponseStr;
                        updateHandler.sendMessage(message);
                        Thread.sleep(10000);
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
    //服务停止，停止子线程的循环
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("WidgetService", "GetDataService->onDestroy");
        isRunning=false;
    }

    public class UpdateHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_MESSAGE:
                    updateWidget(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    }
    //更新小插件Widget的信息的方法
    private void updateWidget(String str) {
        TodayWeather todayWeather=parseXML(str);
        Log.i("Response1",str);
        // 更新 Widget
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.my_app_widget);
        remoteViews.setTextViewText(R.id.tv_city,todayWeather.getCity()+"天气");
        remoteViews.setTextViewText(R.id.tv_degree ,"温度："+todayWeather.getLow()+"~"+todayWeather.getHigh());
        remoteViews.setTextViewText(R.id.tv_weather,todayWeather.getType());
        remoteViews.setTextViewText(R.id.tv_humidity,"湿度："+todayWeather.getShidu());
        remoteViews.setTextViewText(R.id.tv_wind,"风力："+todayWeather.getFengli());
        remoteViews.setTextViewText(R.id.tv_air_quality,"空气质量："+todayWeather.getPm25()+"  "+todayWeather.getQuality());
        remoteViews.setTextViewText(R.id.tv_time,"更新于 "+todayWeather.getDate()+" "+todayWeather.getUpdatetime());
        switchFace(todayWeather.getType(),remoteViews);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(new ComponentName(this, MyAppWidget.class), remoteViews);
    }
    //同MainActivity方法一样，解析获取的天气XML，返回一个TodayWeather对象
    private TodayWeather parseXML(String reponseStr) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(reponseStr));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                            todayWeather.setPm25("");
                            todayWeather.setQuality("");
                        }
                        if (xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                        }  else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        }  else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
    //同MainActivity里方法一样，根据天气情况，更新小插件Widget的天气情况图片
    private void switchFace(String weather,RemoteViews remoteViews) {
        switch (weather) {
            case "阵雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "暴雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "小雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雨":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "中雪":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "雾":
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_wu);
                break;
            default:
                remoteViews.setImageViewResource(R.id.iv_weather_img,R.drawable.biz_plugin_weather_qing);
                break;
        }
    }

}
