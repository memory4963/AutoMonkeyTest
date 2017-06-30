package com.squarecircle.automonkeytest.Activity.Main.Fragment.Monkey;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableRow;

import com.squarecircle.automonkeytest.Activity.Main.Fragment.Env.MonkeyAdapter;
import com.squarecircle.automonkeytest.R;
import com.squarecircle.automonkeytest.Utils.RecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class MonkeyFragment extends Fragment {
    
    @BindView(R.id.monkey_app_list_btn) Button appListBtn;
    @BindView(R.id.monkey_app_list_row) TableRow listRow;
    @BindView(R.id.monkey_monkey_recycler) RecyclerView recyclerView;
    @BindView(R.id.monkey_test_num) EditText textNumEt;
    @BindView(R.id.monkey_create_interval) EditText createIntervalEt;
    @BindView(R.id.monkey_crash_stop) RadioGroup crashRadio;
    @BindView(R.id.monkey_timeout_stop) RadioGroup timeoutRadio;
    @BindView(R.id.monkey_permission_stop) RadioGroup permissionRadio;
    @BindView(R.id.monkey_pct_touch) EditText pctTouchEt;
    @BindView(R.id.monkey_pct_motion) EditText pctMotionEt;
    @BindView(R.id.monkey_exec_btn) Button execBtn;
    
    //列表是否展开
    boolean isListExpanded = false;
    List<String> appList;
    MonkeyAdapter adapter;
    
    public static MonkeyFragment getInstance() {
        return new MonkeyFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_monkey, container, false);
        ButterKnife.bind(this, view);
        
        //app list recyclerView
        if (appList == null) {
            appList = new ArrayList<>();
            // TODO: 2017/6/30 获取手机应用信息
            PackageManager pm = getActivity().getPackageManager();
            
        }
        if (adapter == null) {
            adapter = new MonkeyAdapter(getContext(), appList);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new RecyclerViewDivider(getContext(), LinearLayoutManager.HORIZONTAL));
        
        return view;
    }
    
    @OnClick(R.id.monkey_app_list_btn)
    void onAppListBtnOnClick(View view) {
        if (isListExpanded) {
            isListExpanded = false;
            ((Button) view).setText("折叠");
            listRow.setVisibility(View.VISIBLE);
            
        } else {
            isListExpanded = true;
            ((Button) view).setText("展开");
            listRow.setVisibility(View.GONE);
        }
    }
    
}
