package cn.edu.pku.wangyongsheng.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by xiaosheng on 2017/10/18.
 */

public class SelectCityActivity extends Activity implements View.OnClickListener {
    private ImageView iv_go_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initView();
    }

    private void initView() {
        iv_go_back = findViewById(R.id.iv_go_back);
        iv_go_back.setOnClickListener(this);

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
        getIntent().putExtra("city_code", "101160101");
        setResult(RESULT_OK, getIntent());
        finish();
        super.onBackPressed();
    }
}
