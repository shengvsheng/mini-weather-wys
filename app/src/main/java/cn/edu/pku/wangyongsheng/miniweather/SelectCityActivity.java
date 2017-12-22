package cn.edu.pku.wangyongsheng.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangyongsheng.app.MyApplication;
import cn.edu.pku.wangyongsheng.bean.City;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class SelectCityActivity extends Activity implements View.OnClickListener {
    private ImageView iv_go_back;
    private TextView tv_select_city;
    private ListView lv_city_list;
    private List<City> mCityList;
    private SearchView sv_city;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initView();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        iv_go_back = findViewById(R.id.iv_go_back);
        iv_go_back.setOnClickListener(this);
        tv_select_city = findViewById(R.id.tv_select_city);
        tv_select_city.setText("当前选择城市：" + sharedPreferences.getString("SELECT","北京"));
        lv_city_list = findViewById(R.id.lv_city_list);
        mCityList = MyApplication.getInstance().getCityList();
        sv_city = findViewById(R.id.sv_city);
        //设置该SearchView默认是否自动缩小为图标
        sv_city.setIconifiedByDefault(true);
        //设置该SearchView显示搜索按钮
        sv_city.setSubmitButtonEnabled(true);

        //设置该SearchView内默认显示的提示文本
        sv_city.setQueryHint("查找城市");

        //ListView适配数据
        lv_city_list.setAdapter(new CityListAdapter());
        //ListView的item点击事件
        lv_city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCityActivity.this, mCityList.get(i).getNumber(), Toast.LENGTH_SHORT).show();
                //保存点击的item的位置
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("SELECT", mCityList.get(i).getCity());
                edit.putString("city_code", mCityList.get(i).getNumber());
                edit.commit();
                tv_select_city.setText("当前选择城市：" + mCityList.get(i).getCity());
            }
        });

        //设置该SearchView的监听事件
        sv_city.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryCity(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queryCity(s);
                return true;
            }
        });

    }

    //查找城市并更新listview方法
    private void queryCity(String s) {
        mCityList = compare(s);
        lv_city_list.setAdapter(new CityListAdapter());

    }

    //匹配搜索字段与数据库城市名、代码方法
    private List<City> compare(String s) {
        List<City> list = new ArrayList<>();
        List<City> all = MyApplication.getInstance().getCityList();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getNumber().contains(s) || toPingyin(all.get(i).getCity()).contains(s)||all.get(i).getCity().contains(s)) {
                list.add(all.get(i));
            }
        }
        return list;
    }
    //汉字转拼音方法，使用过TinyPinyin类库
    private String toPingyin(String str) {
        String pingyin = "";
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            pingyin += Pinyin.toPinyin(chars[i]);
        }
        return pingyin.toLowerCase();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_go_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, getIntent());
        finish();
        super.onBackPressed();
    }

    //自定义城市适配器
    class CityListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCityList.size();
        }

        @Override
        public Object getItem(int i) {
            return mCityList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(SelectCityActivity.this).inflate(R.layout.select_city_item, null);
            TextView tv_city_item = view.findViewById(R.id.tv_city_item);
            notifyDataSetChanged();
            tv_city_item.setText(mCityList.get(i).getCity());
            return view;
        }
    }
}
