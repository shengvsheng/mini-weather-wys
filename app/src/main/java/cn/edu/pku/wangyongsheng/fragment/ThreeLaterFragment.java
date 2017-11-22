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

public class ThreeLaterFragment extends Fragment{
    private TextView tv_day_four,tv_day_four_degree,tv_day_four_weather,tv_day_four_wind;
    private ImageView iv_weather_face;
    private SharedPreferences sharedPreferences;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_threeday_later,container,false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        sharedPreferences=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        tv_day_four=view.findViewById(R.id.tv_day_four);
        tv_day_four_degree=view.findViewById(R.id.tv_day_four_degree);
        tv_day_four_weather=view.findViewById(R.id.tv_day_four_weather);
        tv_day_four_wind=view.findViewById(R.id.tv_day_four_wind);
        iv_weather_face=view.findViewById(R.id.iv_day_four_face);
        String fomer=sharedPreferences.getString("SIXDAY","NULL");
        if (!fomer.equals("NULL")){
            List<SixDay> sixDays= JSON.parseArray(fomer,SixDay.class);
                tv_day_four.setText(sixDays.get(3).getDay());
                tv_day_four_degree.setText(sixDays.get(3).getLow()+"~"+sixDays.get(3).getHigh());
                tv_day_four_weather.setText(sixDays.get(3).getWeather());
                tv_day_four_wind.setText(sixDays.get(3).getWind());
            if (sixDays.get(3).getDay() != null) {
                switch (sixDays.get(3).getDay()) {
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
                        iv_weather_face.setImageResource(R.drawable.biz_plugin_weather_qing);
                        break;
                }
            }

        }else {
            for (int i=0;i<3;i++){
                tv_day_four.setText("NULL");
                tv_day_four_degree.setText("NULL");
                tv_day_four_weather.setText("NULL");
                tv_day_four_wind.setText("NULL");
            }
        }
    }
}
