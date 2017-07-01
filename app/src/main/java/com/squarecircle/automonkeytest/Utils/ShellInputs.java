package com.squarecircle.automonkeytest.Utils;

import android.widget.Toast;

import com.squarecircle.automonkeytest.Activity.Base.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cfm on 2017/7/1.
 */

public class ShellInputs {
    
    protected static final String TAG = "ShellInputs";
    
    public static void execute(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String executeForResult(final String command) {
        
        final String[] result = new String[1];
        
        Observable.create(new ObservableOnSubscribe<String>() {
            
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    if (process.waitFor() != 0) {
                        BufferedReader ie =
                                new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        BufferedReader in =
                                new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String error;
                        String errors = "";
                        while ((error = ie.readLine()) != null) {
                            errors += (error + "\n");
                        }
                        String input;
                        String inputs = "";
                        while ((input = in.readLine()) != null) {
                            inputs += (input + "\n");
                        }
                        String str = inputs.equals("") ? errors : inputs;
                        e.onNext(str);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                    e.onError(null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        result[0] = s;
                        if (s != null) {
                            Toast.makeText(App.getContext(), "命令完成", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(App.getContext(), "命令失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(App.getContext(), "命令失败", Toast.LENGTH_SHORT).show();
                        result[0] = null;
                    }
                });
        return result[0];
    }
    
}
