package cn.edu.pku.wangyongsheng.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangyongsheng.bean.SixDay;
import cn.edu.pku.wangyongsheng.bean.TodayWeather;
import cn.edu.pku.wangyongsheng.miniweather.R;
import cn.edu.pku.wangyongsheng.service.UpdateWidgetDataService;

/**
 * 桌面天气小插件
 */
public class MyAppWidget extends AppWidgetProvider {

    //当小插件第一次添加或更新时，启动更新小插件信息的Service
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, UpdateWidgetDataService.class));
    }
    //当小插件被删除时，停止更新小插件信息的Service
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        context.stopService(new Intent(context, UpdateWidgetDataService.class));
    }
}

