package com.squarecircle.automonkeytest.Activity.Main.Fragment.Env;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;

import com.squarecircle.automonkeytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class MonkeyHolder extends ViewHolder {
    
    @BindView(R.id.monkey_list_cb) CheckBox checkBox;
    
    public MonkeyHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
