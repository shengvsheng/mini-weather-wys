package cn.edu.pku.wangyongsheng.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangyongsheng.bean.City;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class CityDB {
    //定义数据库名称和数据表名称
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;
    //打开创建数据库
    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<>();
        //使用sql语句查询数据，数据库游标保存查询结果集
        Cursor cursor = db.rawQuery("select * from " + CITY_TABLE_NAME, null);
        //迭代游标，保存在List里
        while (cursor.moveToNext()) {
            String province = cursor.getString(cursor.getColumnIndex("province"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String allPY = cursor.getString(cursor.getColumnIndex("allpy"));
            String allFirstPY = cursor.getString(cursor.getColumnIndex("allfirstpy"));
            String firstPY = cursor.getString(cursor.getColumnIndex("firstpy" ));
            City item = new City(province, city, number, firstPY, allPY,allFirstPY);
            list.add(item);
        }
        return list;
    }
}
