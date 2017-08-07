package com.squarecircle.automonkeytest.Activity.Main.Fragment.Env;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.squarecircle.automonkeytest.Activity.EnvResult.EnvResultActivity;
import com.squarecircle.automonkeytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class EnvFragment extends Fragment {
    
    public static final String THREAD_COUNT = "threadCount";
    public static final String PICTURE_COUNT = "pictureCount";
    public static final String CPU_INTERVAL = "CPUInterval";
    public static final String MEMORY_INTERVAL = "memoryInterval";
    
    
    @BindView(R.id.env_thread_num) EditText threadNumEt;
    @BindView(R.id.env_picture_num) EditText pictureNumEt;
    @BindView(R.id.env_cpu_interval) EditText cpuIntervalEt;
    @BindView(R.id.env_memory_interval) EditText memoryInterValEt;
    @BindView(R.id.env_exec_btn) Button execBtn;
    
    public static EnvFragment getInstance() {
        return new EnvFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_env, container, false);
        ButterKnife.bind(this, view);
        
        execBtn.setOnClickListener(new View.OnClickListener() {
    
            @Override
            public void onClick(View v) {
                int threadCount = 20, pictureCount = 100, CPUInterval = 3, memoryInterval =
                        3;
                if (!TextUtils.isEmpty(threadNumEt.getText().toString())) {
                    threadCount = Integer.parseInt(threadNumEt.getText().toString());
                }
                if (!TextUtils.isEmpty(pictureNumEt.getText().toString())) {
                    pictureCount = Integer.parseInt(pictureNumEt.getText().toString());
                }
                if (!TextUtils.isEmpty(cpuIntervalEt.getText().toString())) {
                    CPUInterval = Integer.parseInt(cpuIntervalEt.getText().toString());
                }
                if (!TextUtils.isEmpty(memoryInterValEt.getText().toString())) {
                    memoryInterval = Integer.parseInt(memoryInterValEt.getText().toString());
                }
    
                Intent intent = new Intent(getActivity(), EnvResultActivity.class);
    
                intent.putExtra(THREAD_COUNT, threadCount);
                intent.putExtra(PICTURE_COUNT, pictureCount);
                intent.putExtra(CPU_INTERVAL, CPUInterval);
                intent.putExtra(MEMORY_INTERVAL, memoryInterval);
                
                startActivity(intent);
                
            }
        });
        
        return view;
    }
}
