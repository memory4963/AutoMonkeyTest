package com.squarecircle.automonkeytest.Activity.Main.Fragment.Env;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.squarecircle.automonkeytest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memory4963 on 2017/6/30.
 */

public class MonkeyAdapter extends RecyclerView.Adapter<MonkeyHolder> {
    
    private Context context;
    private LayoutInflater inflater;
    private List<String> datas;
    List<Integer> selectedPos;
    
    public MonkeyAdapter(Context context, List<String> datas) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.datas = datas;
        selectedPos = new ArrayList<>();
    }
    
    @Override
    public MonkeyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.monkey_list_child, parent, false);
        return new MonkeyHolder(view);
    }
    
    @Override
    public void onBindViewHolder(MonkeyHolder holder, final int position) {
        //检查是否已选中
        for (Integer i : selectedPos) {
            if (i.equals(position)) {
                holder.checkBox.setSelected(true);
                break;
            }
        }
        //设置文字
        holder.checkBox.setText(datas.get(position));
        //设置监听器
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedPos.add(position);
                } else {
                    for (Integer i : selectedPos) {
                        if (i.equals(position)) {
                            selectedPos.remove(i);
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return 0;
    }
}
