package cn.edu.pku.wangyongsheng.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.edu.pku.wangyongsheng.util.NetUtil;

/**
 * Created by xiaosheng on 2017/9/22.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_title_city, tv_city, tv_time, tv_humidity, tv_daytime, tv_pm2_5_value, tv_pm2_5_quality,
            tv_temp, tv_weather, tv_wind, tv_degree;
    private ImageView iv_pm2_5_face, iv_weather_face, iv_title_update, iv_select_city;
    private static final int UPDATE_TODAY_WEATHER = 1;

    private Handler mHandler;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        initView();
        addHandle();
    }

    private void addHandle() {
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPDATE_TODAY_WEATHER:
                        updateTodayWeather((TodayWeather) msg.obj);

                        break;
                    default:
                        break;
                }
            }

        };
    }

    private void updateTodayWeather(TodayWeather todayWeather) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("title_city" , todayWeather.getCity() + "天气");
        edit.putString("city" , todayWeather.getCity());
        edit.putString("time" , todayWeather.getUpdatetime() + "发布");
        edit.putString("humidity" , "湿度：" + todayWeather.getShidu());
        if (todayWeather.getPm25().equals("")) {
            edit.putString("pm2_5_value" , "无");
            edit.putString("pm2_5_quality" , "无");
        } else {
            edit.putString("pm2_5_value" , todayWeather.getPm25());
            edit.putString("pm2_5_quality" , todayWeather.getQuality());
        }
        edit.putString("daytime" , todayWeather.getDate());
        edit.putString("temp" , "温度:" + todayWeather.getLow() + "~" + todayWeather.getHigh());
        edit.putString("degree" , todayWeather.getLow() + "~" + todayWeather.getHigh());
        edit.putString("weather" , todayWeather.getType());
        edit.putString("wind" , "风力:" + todayWeather.getFengli());
        edit.commit();
        tv_title_city.setText(todayWeather.getCity() + "天气");
        tv_city.setText(todayWeather.getCity());
        tv_time.setText(todayWeather.getUpdatetime() + "发布");
        tv_humidity.setText("湿度：" + todayWeather.getShidu());

        tv_daytime.setText(todayWeather.getDate());
        tv_temp.setText("温度:" + todayWeather.getLow() + "~" + todayWeather.getHigh());
        tv_degree.setText(todayWeather.getLow() + "~" + todayWeather.getHigh());
        tv_weather.setText(todayWeather.getType());
        tv_wind.setText("风力:" + todayWeather.getFengli());
        if (todayWeather.getPm25().equals("")) {
            tv_pm2_5_value.setText("无");
            tv_pm2_5_quality.setText("无");
        } else {
            tv_pm2_5_value.setText(todayWeather.getPm25());
            tv_pm2_5_quality.setText(todayWeather.getQuality());
        }
        switchFace(todayWeather.getType(), todayWeather.getQuality());
        Toast.makeText(MainActivity.this, "更新成功！" , Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        sharedPreferences = getSharedPreferences("config" , MODE_PRIVATE);

        iv_title_update = findViewById(R.id.iv_title_update);
        iv_select_city = findViewById(R.id.iv_select_city);
        iv_title_update.setOnClickListener(this);
        iv_select_city.setOnClickListener(this);

        tv_title_city = findViewById(R.id.tv_title_city);
        tv_city = findViewById(R.id.tv_city);
        tv_time = findViewById(R.id.tv_time);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_daytime = findViewById(R.id.tv_daytime);
        tv_pm2_5_value = findViewById(R.id.tv_pm2_5_value);
        tv_pm2_5_quality = findViewById(R.id.tv_pm2_5_quality);
        iv_pm2_5_face = findViewById(R.id.iv_pm2_5_face);
        tv_temp = findViewById(R.id.tv_temp);
        tv_weather = findViewById(R.id.tv_weather);
        tv_wind = findViewById(R.id.tv_wind);
        tv_degree = findViewById(R.id.tv_degree);
        iv_weather_face = findViewById(R.id.iv_weather_face);


        tv_title_city.setText(sharedPreferences.getString("title_city" , "N/A"));
        tv_city.setText(sharedPreferences.getString("city" , "N/A"));
        tv_time.setText(sharedPreferences.getString("time" , "N/A"));
        tv_humidity.setText(sharedPreferences.getString("humidity" , "N/A"));
        tv_daytime.setText(sharedPreferences.getString("daytime" , "N/A"));
        tv_pm2_5_value.setText(sharedPreferences.getString("pm2_5_value" , "N/A"));
        tv_pm2_5_quality.setText(sharedPreferences.getString("pm2_5_quality" , "N/A"));
        tv_temp.setText(sharedPreferences.getString("temp" , "N/A"));
        tv_weather.setText(sharedPreferences.getString("weather" , "N/A"));
        tv_wind.setText(sharedPreferences.getString("wind" , "N/A"));
        tv_degree.setText(sharedPreferences.getString("degree" , "N/A"));
        switchFace(sharedPreferences.getString("weather" , "N/A"), sharedPreferences.getString("pm2_5_quality" , "N/A"));
    }

    private void switchFace(String weather, String pm2_5_quality) {

        switch (weather) {
            case "阵雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "暴雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "小雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "中雨":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "中雪":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "雾":
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            default:
                iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
        }
        switch (pm2_5_quality) {
            case "优":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_0_50);
                break;
            case "良":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_51_100);
                break;
            case "轻度污染":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_101_150);
                break;
            case "中度污染":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_151_200);
                break;
            case "重度污染":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_201_300);
                break;
            case "严重污染":
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_greater_300);
                break;
            default:
                iv_pm2_5_face.setImageResource(R.drawable.biz_plugin_weather_0_50);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_select_city) {
            Intent i = new Intent();
            i.setClass(this, SelectCityActivity.class);
            startActivityForResult(i, 0);
        }

        if (view.getId() == R.id.iv_title_update) {

            String cityCode = sharedPreferences.getString("city_code" , "101010100");
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                queryWeatherInfo(cityCode);
            } else {
                Toast.makeText(this, "没有网络，请打开网络设置" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void queryWeatherInfo(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
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
                    String reponseStr = reponse.toString();
                    TodayWeather todayWeaher = parseXML(reponseStr);
                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeaher;
                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "网络异常！" , Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }

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
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
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
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("city_code" , data.getStringExtra("city_code"));
                    edit.commit();
                    Log.i("SSSSSCODE" , data.getStringExtra("city_code"));
                    if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                        queryWeatherInfo(data.getStringExtra("city_code"));
                    } else {
                        Toast.makeText(this, "没有网络，请打开网络设置" , Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
