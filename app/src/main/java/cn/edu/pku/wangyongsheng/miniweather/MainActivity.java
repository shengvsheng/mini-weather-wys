package cn.edu.pku.wangyongsheng.miniweather;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import cn.edu.pku.wangyongsheng.bean.SixDay;
import cn.edu.pku.wangyongsheng.bean.TodayWeather;
import cn.edu.pku.wangyongsheng.fragment.ThreeFormerFragment;
import cn.edu.pku.wangyongsheng.fragment.ThreeLaterFragment;
import cn.edu.pku.wangyongsheng.service.GetDataService;
import cn.edu.pku.wangyongsheng.util.MyFragmentPageAdapter;
import cn.edu.pku.wangyongsheng.util.NetUtil;

/**
 * Created by xiaosheng on 2017/9/22.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_title_city, tv_city, tv_time, tv_humidity, tv_daytime, tv_pm2_5_value, tv_pm2_5_quality,
            tv_temp, tv_weather, tv_wind, tv_degree;
    private ImageView iv_pm2_5_face, iv_weather_face, iv_title_update, iv_select_city, iv_first_page, iv_sencond_page;
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ProgressBar pb_update;

    private ViewPager vp_six_weather;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    private List<Fragment> fragmentList;
    DataReceiver dataReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        addBroadcast();
        initView();
    }

    private void addBroadcast() {
        startService(new Intent(this, GetDataService.class));
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        filter.addAction("cn.pku.ui.service");
        registerReceiver(dataReceiver, filter);// 注册Broadcast Receiver

    }


    //使用Handler更新主线程UI
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    if ((TodayWeather) msg.obj == null) {
                        Log.i("City", "IS null");
                    } else {
                        updateTodayWeather((TodayWeather) msg.obj);
                    }

                    iv_title_update.setVisibility(View.VISIBLE);
                    pb_update.setVisibility(View.GONE);
                    updateSixWeather();
                    break;
                default:
                    break;
            }
        }

    };

    private void updateTodayWeather(TodayWeather todayWeather) {
        //更新UI以及更新数据时使用SharedPreferences保存数据，供下次打开时使用
        if (todayWeather.getCity() == null) {
            tv_title_city.setText("当前城市错误");
            tv_city.setText("无");
            tv_time.setText("无");
            tv_humidity.setText("湿度：无");
            tv_daytime.setText("无");
            tv_temp.setText("无");
            tv_degree.setText("无");
            tv_weather.setText("无");
            tv_wind.setText("风力:无");
            edit.putString("title_city", "当前城市错误");
            edit.putString("city", "无");
            edit.putString("time", "无");
            edit.putString("humidity", "湿度：无");
            edit.putString("pm2_5_value", "无");
            edit.putString("pm2_5_quality", "无");
            edit.putString("daytime", "无");
            edit.putString("temp", "温度:无");
            edit.putString("degree", "无");
            edit.putString("weather", "无");
            edit.putString("wind", "风力:无");
            edit.commit();
        } else {
            edit.putString("title_city", todayWeather.getCity() + "天气");
            edit.putString("city", todayWeather.getCity());
            edit.putString("time", todayWeather.getUpdatetime() + "发布");
            edit.putString("humidity", "湿度：" + todayWeather.getShidu());
            if (todayWeather.getPm25().equals("")) {
                edit.putString("pm2_5_value", "无");
                edit.putString("pm2_5_quality", "无");
            } else {
                edit.putString("pm2_5_value", todayWeather.getPm25());
                edit.putString("pm2_5_quality", todayWeather.getQuality());
            }
            edit.putString("daytime", todayWeather.getDate());
            edit.putString("temp", "温度:" + todayWeather.getLow() + "~" + todayWeather.getHigh());
            edit.putString("degree", todayWeather.getLow() + "~" + todayWeather.getHigh());
            edit.putString("weather", todayWeather.getType());
            edit.putString("wind", "风力:" + todayWeather.getFengli());
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
            //更新成功Toast信息
            Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        }


    }

    //更新六天天气数据
    private void updateSixWeather() {
        Fragment former = new ThreeFormerFragment();
        Fragment later = new ThreeLaterFragment();
        fragmentList.add(former);
        fragmentList.add(later);
        FragmentManager fm = getSupportFragmentManager();
        MyFragmentPageAdapter newsFragmentPageAdapter = new MyFragmentPageAdapter(fm, fragmentList); //new myFragmentPagerAdater记得带上两个参数
        vp_six_weather.setAdapter(newsFragmentPageAdapter);
        vp_six_weather.setCurrentItem(0);
        vp_six_weather.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    iv_first_page.setImageResource(R.drawable.page_indicator_focused);
                    iv_sencond_page.setImageResource(R.drawable.page_indicator_unfocused);
                }
                if (position == 1) {
                    iv_first_page.setImageResource(R.drawable.page_indicator_unfocused);
                    iv_sencond_page.setImageResource(R.drawable.page_indicator_focused);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //初始化控件方法
    private void initView() {

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        edit = sharedPreferences.edit();

        //绑定控件
        vp_six_weather = findViewById(R.id.vp_six_weather);
        iv_title_update = findViewById(R.id.iv_title_update);
        iv_select_city = findViewById(R.id.iv_select_city);
        iv_title_update.setOnClickListener(this);
        iv_select_city.setOnClickListener(this);
        pb_update = findViewById(R.id.pb_update);
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
        iv_first_page = findViewById(R.id.iv_first_page);
        iv_sencond_page = findViewById(R.id.iv_second_page);
        fragmentList = new ArrayList<>();
        //从SharedPreferences中获取数据，并更新控件的内容
        tv_title_city.setText(sharedPreferences.getString("title_city", "N/A"));
        tv_city.setText(sharedPreferences.getString("city", "N/A"));
        tv_time.setText(sharedPreferences.getString("time", "N/A"));
        tv_humidity.setText(sharedPreferences.getString("humidity", "N/A"));
        tv_daytime.setText(sharedPreferences.getString("daytime", "N/A"));
        tv_pm2_5_value.setText(sharedPreferences.getString("pm2_5_value", "N/A"));
        tv_pm2_5_quality.setText(sharedPreferences.getString("pm2_5_quality", "N/A"));
        tv_temp.setText(sharedPreferences.getString("temp", "N/A"));
        tv_weather.setText(sharedPreferences.getString("weather", "N/A"));
        tv_wind.setText(sharedPreferences.getString("wind", "N/A"));
        tv_degree.setText(sharedPreferences.getString("degree", "N/A"));
        switchFace(sharedPreferences.getString("weather", "N/A"), sharedPreferences.getString("pm2_5_quality", "N/A"));
        updateSixWeather();
    }

    //根据天气、空气质量情况更新脸的表情和天气的表情
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
        //点击选择城市控件，跳转到选择城市activity
        if (view.getId() == R.id.iv_select_city) {
            Intent i = new Intent();
            i.setClass(this, SelectCityActivity.class);
            startActivityForResult(i, 0);
        }
        //点击获取数据控件，获取当前保存的城市代码对应的城市天气数据
        if (view.getId() == R.id.iv_title_update) {

            String cityCode = sharedPreferences.getString("city_code", "101010100");
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                queryWeatherInfo(cityCode);

            } else {
                Toast.makeText(this, "没有网络，请打开网络设置", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //根据城市代码查询天气情况
    private void queryWeatherInfo(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        iv_title_update.setVisibility(View.GONE);
        pb_update.setVisibility(View.VISIBLE);
        //使用子线程，通过Http方式获取接口数据
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
                    //解析XML数据到天气对象中
                    TodayWeather todayWeaher = parseXML(reponseStr);
                    //使用Handle提醒主线程更新UI
                    Message msg = new Message();
                    msg.what = UPDATE_TODAY_WEATHER;
                    msg.obj = todayWeaher;
                    mHandler.sendMessageDelayed(msg, 4000);
//                    mHandler.sendMessage(msg);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析XML文件数据
    private TodayWeather parseXML(String reponseStr) {
        TodayWeather todayWeather = null;
        SixDay sixDay_one = null;
        SixDay sixDay_two = null;
        SixDay sixDay_three = null;
        SixDay sixDay_four = null;
        List<SixDay> sixDays = new ArrayList<>();
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
                            sixDay_one = new SixDay();
                            sixDay_two = new SixDay();
                            sixDay_three = new SixDay();
                            sixDay_four = new SixDay();
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
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 1) {
                            eventType = xmlPullParser.next();
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 2) {
                            eventType = xmlPullParser.next();
                            sixDay_one.setWind(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 3) {
                            eventType = xmlPullParser.next();
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 4) {
                            eventType = xmlPullParser.next();
                            sixDay_two.setWind(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 5) {
                            eventType = xmlPullParser.next();
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 6) {
                            eventType = xmlPullParser.next();
                            sixDay_three.setWind(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 7) {
                            eventType = xmlPullParser.next();
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 8) {
                            eventType = xmlPullParser.next();
                            sixDay_four.setWind(xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                            eventType = xmlPullParser.next();
                            sixDay_one.setDay(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                            eventType = xmlPullParser.next();
                            sixDay_two.setDay(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                            eventType = xmlPullParser.next();
                            sixDay_three.setDay(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 4) {
                            eventType = xmlPullParser.next();
                            sixDay_four.setDay(xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                            eventType = xmlPullParser.next();
                            sixDay_one.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                            eventType = xmlPullParser.next();
                            sixDay_two.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                            eventType = xmlPullParser.next();
                            sixDay_three.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                            eventType = xmlPullParser.next();
                            sixDay_four.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                            eventType = xmlPullParser.next();
                            sixDay_one.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                            eventType = xmlPullParser.next();
                            sixDay_two.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                            eventType = xmlPullParser.next();
                            sixDay_three.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                            eventType = xmlPullParser.next();
                            sixDay_four.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                            eventType = xmlPullParser.next();

                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                            eventType = xmlPullParser.next();
                            sixDay_one.setWeather(xmlPullParser.getText());
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                            eventType = xmlPullParser.next();
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                            eventType = xmlPullParser.next();
                            sixDay_two.setWeather(xmlPullParser.getText());
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 5) {
                            eventType = xmlPullParser.next();
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 6) {
                            eventType = xmlPullParser.next();
                            sixDay_three.setWeather(xmlPullParser.getText());
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 7) {
                            eventType = xmlPullParser.next();
                            typeCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 8) {
                            eventType = xmlPullParser.next();
                            sixDay_four.setWeather(xmlPullParser.getText());
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
        sixDays.add(sixDay_one);
        sixDays.add(sixDay_two);
        sixDays.add(sixDay_three);
        sixDays.add(sixDay_four);
        edit.putString("SIXDAY", JSON.toJSONString(sixDays));
        edit.commit();
        return todayWeather;
    }

    //对返回的activity，通过requestcode判断作更新操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("city_code", data.getStringExtra("city_code"));
                    edit.commit();
                    Log.i("SSSSSCODE", data.getStringExtra("city_code"));
                    if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                        queryWeatherInfo(data.getStringExtra("city_code"));
                    } else {
                        Toast.makeText(this, "没有网络，请打开网络设置", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("data");
            Log.i("respom", response);
            updateTodayWeather(parseXML(response));
            updateSixWeather();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Activity","resume!!!");
        startService(new Intent(this, GetDataService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Activity","stop!!!");
        stopService(new Intent(this, GetDataService.class));
    }

}
