package com.squarecircle.automonkeytest.Activity.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.squarecircle.automonkeytest.Activity.Main.Fragment.Env.EnvFragment;
import com.squarecircle.automonkeytest.Activity.Main.Fragment.Monkey.MonkeyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class MainPagerAdapter extends FragmentPagerAdapter{
    
    List<Fragment> fragments;
    List<CharSequence> titles;
    
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(EnvFragment.getInstance());
        fragments.add(MonkeyFragment.getInstance());
    
        titles = new ArrayList<>();
        titles.add("恶劣环境");
        titles.add("Monkey");
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
    
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    
    @Override
    public int getCount() {
        return fragments.size();
    }
}
