package cn.edu.pku.wangyongsheng.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.edu.pku.wangyongsheng.miniweather.R;

/**
 * Created by xiaosheng on 2017/11/29.
 */


public class GuidePageOneFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_guide_one,container,false);
       initView();
       return view;
    }

    private void initView() {
    }
}
