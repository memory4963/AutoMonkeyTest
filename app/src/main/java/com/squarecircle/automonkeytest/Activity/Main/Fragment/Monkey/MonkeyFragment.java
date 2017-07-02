package com.squarecircle.automonkeytest.Activity.Main.Fragment.Monkey;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;

import com.squarecircle.automonkeytest.Activity.MonkeyResult.MonkeyResultActivity;
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
    
    protected final String TAG = this.getClass().getSimpleName();
    
    @BindView(R.id.monkey_app_list_btn) Button appListBtn;
    @BindView(R.id.monkey_app_list_row) TableRow listRow;
    @BindView(R.id.monkey_monkey_recycler) RecyclerView recyclerView;
    @BindView(R.id.monkey_test_num) EditText testNum;
    @BindView(R.id.monkey_create_interval) EditText intervalEt;
    @BindView(R.id.monkey_crash_stop) RadioGroup crashRadio;
    @BindView(R.id.monkey_timeout_stop) RadioGroup timeoutRadio;
    @BindView(R.id.monkey_permission_stop) RadioGroup permissionRadio;
    @BindView(R.id.monkey_pct_touch) EditText pctTouchEt;
    @BindView(R.id.monkey_pct_motion) EditText pctMotionEt;
    @BindView(R.id.monkey_exec_btn) Button execBtn;
    
    //列表是否展开
    boolean isListExpanded = false;
    List<AppInfo> appList;
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
        } else {
            appList.clear();
        }
        getAppList();
        
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
    
    private void getAppList() {
        PackageManager pm = this.getActivity().getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 取得非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppInfo info = new AppInfo();
                info.appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                info.pkgName = packageInfo.packageName;
                info.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                // 获取该应用安装包的Intent，用于启动该应用
                info.appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);
                appList.add(info);
            }
        }
    }
    
    @OnClick(R.id.monkey_app_list_btn)
    void onAppListBtnOnClick(View view) {
        if (isListExpanded) {
            isListExpanded = false;
            ((Button) view).setText("展开");
            listRow.setVisibility(View.GONE);
            
        } else {
            isListExpanded = true;
            ((Button) view).setText("折叠");
            listRow.setVisibility(View.VISIBLE);
        }
    }
    
    @OnClick(R.id.monkey_exec_btn)
    void onExecBtnOnClick(View view) {
        MonkeyResultActivity.start(getContext(), generateCommand());
    }
    
    private String generateCommand() {
        
        String packages = "";
        String num = " -v -v -v 100";
        String interval = " --throttle 10";
        String touch = " --pct-touch 15";
        String motion = " --pct-motion 10";
        String crash = "";
        String timeout = "";
        String permission = "";
        
        if (adapter.selectedPos.size() > 0) {
            packages += " -p";
            for (int i : adapter.selectedPos) {
                packages += (" " + appList.get(i).pkgName);
            }
        }
        if (!TextUtils.isEmpty(testNum.getText().toString())) {
            num = " -v -v -v " + Integer.parseInt(testNum.getText().toString());
        }
        if (!TextUtils.isEmpty(intervalEt.getText().toString())) {
            interval = " --throttle " + Integer.parseInt(intervalEt.getText().toString());
        }
        if (!TextUtils.isEmpty(pctTouchEt.getText().toString())) {
            touch = " --pct-touch " + Integer.parseInt(pctTouchEt.getText().toString());
        }
        if (!TextUtils.isEmpty(pctMotionEt.getText().toString())) {
            motion = " --pct-motion " + Integer.parseInt(pctMotionEt.getText().toString());
        }
        for (int i = 0; i < crashRadio.getChildCount(); i++) {
            RadioButton rb = (RadioButton) crashRadio.getChildAt(i);
            if (rb.isChecked()) {
                if (TextUtils.equals("是", rb.getText().toString())) {
                    crash = " --ignore-crashes";
                }
                break;
            }
        }
        for (int i = 0; i < timeoutRadio.getChildCount(); i++) {
            RadioButton rb = (RadioButton) timeoutRadio.getChildAt(i);
            if (rb.isChecked()) {
                if (TextUtils.equals("是", rb.getText().toString())) {
                    timeout = " --ignore-timeouts";
                }
                break;
            }
        }
        for (int i = 0; i < permissionRadio.getChildCount(); i++) {
            RadioButton rb = (RadioButton) permissionRadio.getChildAt(i);
            if (rb.isChecked()) {
                if (TextUtils.equals("是", rb.getText().toString())) {
                    permission = " --ignore-security-exception";
                }
                break;
            }
        }
        
        return "su -c monkey" + packages + crash + timeout + permission + interval + touch + motion
                + num;
    }
    
}
