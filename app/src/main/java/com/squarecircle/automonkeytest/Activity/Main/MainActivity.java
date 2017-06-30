package com.squarecircle.automonkeytest.Activity.Main;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.squarecircle.automonkeytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_tab_layout) TabLayout tabLayout;
    @BindView(R.id.main_view_pager) ViewPager viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    
        setSupportActionBar(toolbar);
        
        
    
        tabLayout.addTab(tabLayout.newTab().setText("恶劣环境"));
        tabLayout.addTab(tabLayout.newTab().setText("Monkey"));
        tabLayout.setupWithViewPager(viewPager);
        
    }
}
