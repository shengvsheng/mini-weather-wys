package cn.edu.pku.wangyongsheng.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.List;

import cn.edu.pku.wangyongsheng.bean.SixDay;
import cn.edu.pku.wangyongsheng.miniweather.R;

/**
 * Created by xiaosheng on 2017/11/22.
 */

public class ThreeFormerFragment extends Fragment {

    private TextView tv_day_one, tv_day_one_degree, tv_day_one_weather, tv_day_one_wind;
    private TextView tv_day_two, tv_day_two_degree, tv_day_two_weather, tv_day_two_wind;
    private TextView tv_day_three, tv_day_three_degree, tv_day_three_weather, tv_day_three_wind;
    private ImageView iv_day_one, iv_day_two, iv_day_three;
    private SharedPreferences sharedPreferences;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_threeday_former, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {

        sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        tv_day_one = view.findViewById(R.id.tv_day_one);
        tv_day_one_degree = view.findViewById(R.id.tv_day_one_degree);
        tv_day_one_weather = view.findViewById(R.id.tv_day_one_weather);
        tv_day_one_wind = view.findViewById(R.id.tv_day_one_wind);
        tv_day_two = view.findViewById(R.id.tv_day_two);
        tv_day_two_degree = view.findViewById(R.id.tv_day_two_degree);
        tv_day_two_weather = view.findViewById(R.id.tv_day_two_weather);
        tv_day_two_wind = view.findViewById(R.id.tv_day_two_wind);
        tv_day_three = view.findViewById(R.id.tv_day_three);
        tv_day_three_degree = view.findViewById(R.id.tv_day_three_degree);
        tv_day_three_weather = view.findViewById(R.id.tv_day_three_weather);
        tv_day_three_wind = view.findViewById(R.id.tv_day_three_wind);
        iv_day_one = view.findViewById(R.id.iv_day_one_face);
        iv_day_two = view.findViewById(R.id.iv_day_two_face);
        iv_day_three = view.findViewById(R.id.iv_day_three_face);
        TextView[] day = {tv_day_one, tv_day_two, tv_day_three};
        TextView[] weather = {tv_day_one_weather, tv_day_two_weather, tv_day_three_weather};
        TextView[] degree = {tv_day_one_degree, tv_day_two_degree, tv_day_three_degree};
        TextView[] wind = {tv_day_one_wind, tv_day_two_wind, tv_day_three_wind};
        ImageView[] face = {iv_day_one, iv_day_two, iv_day_three};
        String fomer = sharedPreferences.getString("SIXDAY", "NULL");
        if (!fomer.equals("NULL")) {
            List<SixDay> sixDays = JSON.parseArray(fomer, SixDay.class);
            for (int i = 0; i < 3; i++) {
                day[i].setText(sixDays.get(i).getDay());
                weather[i].setText(sixDays.get(i).getWeather());
                degree[i].setText(sixDays.get(i).getLow() + "~" + sixDays.get(i).getHigh());
                wind[i].setText(sixDays.get(i).getWind());
                if (sixDays.get(i).getDay() != null) {
                    switch (sixDays.get(i).getWeather()) {
                        case "阵雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                            break;
                        case "暴雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_baoxue);
                            break;
                        case "暴雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_baoyu);
                            break;
                        case "大暴雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                            break;
                        case "大雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_daxue);
                            break;
                        case "大雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_dayu);
                            break;
                        case "多云":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_duoyun);
                            break;
                        case "雷阵雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                            break;
                        case "雷阵雨冰雹":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                            break;
                        case "晴":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_qing);
                            break;
                        case "沙尘暴":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                            break;
                        case "特大暴雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                            break;
                        case "小雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                            break;
                        case "小雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                            break;
                        case "阴":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_yin);
                            break;
                        case "雨夹雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                            break;
                        case "阵雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                            break;
                        case "中雨":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                            break;
                        case "中雪":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                            break;
                        case "雾":
                            face[i].setImageResource(R.drawable.biz_plugin_weather_wu);
                            break;
                        default:
                            face[i].setImageResource(R.drawable.biz_plugin_weather_qing);
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                day[i].setText("NULL");
                weather[i].setText("NULL");
                degree[i].setText("NULL");
                wind[i].setText("NULL");
            }
        }
    }
}
