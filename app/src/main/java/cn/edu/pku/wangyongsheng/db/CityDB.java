package cn.edu.pku.wangyongsheng.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class CityDB {
    private static final String CITY_DB_NAME="city.db";
    private static final String CITY_TABLE_NAME="city";
    private SQLiteDatabase db;

    public CityDB(Context context,String path) {
        db=context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
    }
}
