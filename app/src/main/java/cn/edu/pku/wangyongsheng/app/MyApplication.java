package cn.edu.pku.wangyongsheng.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangyongsheng.bean.City;
import cn.edu.pku.wangyongsheng.db.CityDB;

import static android.content.ContentValues.TAG;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class MyApplication extends Application {
    private static final String APP_TAG="myApp";
    private static MyApplication myApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(APP_TAG,"MyApplication->Create");
        myApplication=this;
        mCityDB=openCityDB();
        initCityList();
    }
    public static MyApplication getInstance(){
        return myApplication;
    }
    //打开apk的目录下的数据库，若不存在，通过输入流从资源assets文件夹下读取数据库文件到apk安装的目录的指定位置
    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {

            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
    private void initCityList(){
        mCityList=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).run();

    }
    //在app启动好就准备好city数据
    private boolean prepareCityList() {
        mCityList=mCityDB.getAllCity();
        int i=0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName);
        }
        Log.d(TAG,"i="+i);
        return true;
    }
    public List<City> getCityList(){
        return mCityList;
    }
}
