package cn.edu.pku.wangyongsheng.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by xiaosheng on 2017/11/22.
 */

public class MyFragmentPageAdapter extends FragmentPagerAdapter {
    private FragmentManager fragmetnmanager;
    private List<Fragment> listfragment;


    public MyFragmentPageAdapter(FragmentManager fm, List<Fragment> listfragment) {
        super(fm);
        this.fragmetnmanager = fm;
        this.listfragment = listfragment;
    }

    @Override
    public Fragment getItem(int arg0) {
        return listfragment.get(arg0);
    }

    @Override
    public int getCount() {
        return listfragment.size();
    }
}
