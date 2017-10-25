package cn.edu.pku.wangyongsheng.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        tv_select_city=findViewById(R.id.tv_select_city);
        lv_city_list=findViewById(R.id.lv_city_list);
        mCityList= MyApplication.getInstance().getCityList();
        lv_city_list.setAdapter(new CityListAdapter());
        lv_city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCityActivity.this, mCityList.get(i).getNumber() , Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putInt("position",i);
                edit.commit();
                tv_select_city.setText("当前选择城市："+mCityList.get(i).getCity());
            }
        });
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
        getIntent().putExtra("city_code",mCityList.get(sharedPreferences.getInt("position",0)).getNumber());
        setResult(RESULT_OK, getIntent());
        finish();
        super.onBackPressed();
    }

    class CityListAdapter extends BaseAdapter{

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
            view= LayoutInflater.from(SelectCityActivity.this).inflate(R.layout.select_city_item,null);
            TextView tv_city_item=view.findViewById(R.id.tv_city_item);
            tv_city_item.setText(mCityList.get(i).getCity());
            return view;
        }
    }
}
