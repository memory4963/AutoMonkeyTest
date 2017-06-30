package com.squarecircle.automonkeytest.Activity.Main.Fragment.Monkey;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squarecircle.automonkeytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class MonkeyHolder extends ViewHolder {
    
    @BindView(R.id.monkey_list_cb) CheckBox checkBox;
    @BindView(R.id.monkey_list_icon) ImageView iconIv;
    @BindView(R.id.monkey_list_name) TextView nameTv;
    
    public MonkeyHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
