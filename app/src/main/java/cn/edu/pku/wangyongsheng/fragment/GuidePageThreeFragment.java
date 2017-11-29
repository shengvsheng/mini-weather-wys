package cn.edu.pku.wangyongsheng.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.edu.pku.wangyongsheng.miniweather.GuidePage;
import cn.edu.pku.wangyongsheng.miniweather.MainActivity;
import cn.edu.pku.wangyongsheng.miniweather.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xiaosheng on 2017/11/29.
 */


public class GuidePageThreeFragment extends Fragment implements View.OnClickListener{
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private Button btn_start;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_guide_three,container,false);
       initView(view);
       return view;
    }

    private void initView(View view) {
        btn_start=view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        sharedPreferences = getActivity().getSharedPreferences("GUIDE_PAGE", MODE_PRIVATE);
        edit = sharedPreferences.edit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_start){

            edit.putBoolean("GUIDE_FLAG",true);
            edit.commit();
            Intent i=new Intent();
            i.setClass(getActivity(),MainActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }
}
