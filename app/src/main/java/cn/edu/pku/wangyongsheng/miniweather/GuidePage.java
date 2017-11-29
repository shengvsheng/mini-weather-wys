package cn.edu.pku.wangyongsheng.miniweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangyongsheng.fragment.GuidePageOneFragment;
import cn.edu.pku.wangyongsheng.fragment.GuidePageThreeFragment;
import cn.edu.pku.wangyongsheng.fragment.GuidePageTwoFragment;
import cn.edu.pku.wangyongsheng.util.MyFragmentPageAdapter;

/**
 * Created by xiaosheng on 2017/11/29.
 */

public class GuidePage extends FragmentActivity {
    private ViewPager vp_guide;
    private List<Fragment> fragmentList;
    private ImageView iv_guide_1_page, iv_guide_2_page, iv_guide_3_page;
    private ImageView[] imageViews;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_page);
        initView();
        initData();
    }

    private void initView() {


        vp_guide = findViewById(R.id.vp_guide);
        iv_guide_1_page = findViewById(R.id.iv_guide_1_page);
        iv_guide_2_page = findViewById(R.id.iv_guide_2_page);
        iv_guide_3_page = findViewById(R.id.iv_guide_3_page);
        sharedPreferences = getSharedPreferences("GUIDE_PAGE", MODE_PRIVATE);
        edit = sharedPreferences.edit();
    }

    private void initData() {
        boolean flag = sharedPreferences.getBoolean("GUIDE_FLAG", false);
        if (!flag){
            fragmentList = new ArrayList<>();
            imageViews = new ImageView[]{iv_guide_1_page, iv_guide_2_page, iv_guide_3_page};
            Fragment one = new GuidePageOneFragment();
            Fragment two = new GuidePageTwoFragment();
            Fragment three = new GuidePageThreeFragment();
            fragmentList.add(one);
            fragmentList.add(two);
            fragmentList.add(three);
            FragmentManager fm = getSupportFragmentManager();
            MyFragmentPageAdapter myFragmentPageAdapter = new MyFragmentPageAdapter(fm, fragmentList); //new myFragmentPagerAdater记得带上两个参数
            vp_guide.setAdapter(myFragmentPageAdapter);
            vp_guide.setCurrentItem(0);
            vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < imageViews.length; i++) {
                        if (i == position) {
                            imageViews[i].setImageResource(R.drawable.page_indicator_focused);
                        } else {
                            imageViews[i].setImageResource(R.drawable.page_indicator_unfocused);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }else {
            Intent i=new Intent();
            i.setClass(GuidePage.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}
