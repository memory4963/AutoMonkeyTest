package com.squarecircle.automonkeytest.Utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.squarecircle.automonkeytest.Activity.MonkeyResult.MonkeyResultActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cfm on 2017/7/1.
 */

public class ShellInputs {

    protected static final String TAG = "ShellInputs";

    public static void executeForResult(final String command, final Handler handler) {

        final String[] result = new String[1];
        final FileSaver fileSaver = new FileSaver();

        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                try {
                    Log.d(TAG, "subscribe: " + command);
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                    BufferedReader ie =
                            new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String error;
                    String errors = "";
                    while ((error = ie.readLine()) != null) {
                        errors += (error + "\n");
                    }
                    Log.d(TAG, "subscribe: error:" + errors);
                    String input;
                    String inputs = "";
                    while ((input = in.readLine()) != null) {
                        inputs += (input + "\n");
                    }
                    String filePath;
                    //输出至文件
                    if (!inputs.equals("")) {
                        filePath = fileSaver.saveToFile("result.txt", inputs);
                    } else {
                        filePath = fileSaver.saveToFile("error.txt", error);
                    }



                    LogParser logParser = new LogParser(inputs);
                    String droppedString = logParser.vectorToString(logParser.getDropedVector());
                    String eventString = logParser.vectorToString(logParser.getEventVector());
                    String exceptionString = logParser.vectorToString(logParser.getExceptionVector());
                    String notUsingString = logParser.vectorToString(logParser.getNotUsingVector());
                    String resultString = "dropped: " + droppedString + "\nevents: " + eventString + "\nexception: " + exceptionString + "\nnotUsing: " + notUsingString;

                    String str = inputs.equals("") ? errors : resultString;

                    Log.d(TAG, "subscribe: input:" + resultString);

                    e.onNext(str);
                } catch (Exception err) {
                    err.printStackTrace();
                    e.onError(null);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        result[0] = s;
                        sendMessage(handler, result[0], MonkeyResultActivity.MONKEY_CALLBACK);
                    }
                }, new Consumer<Throwable>() {

                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        result[0] = null;
                        sendMessage(handler, result[0], MonkeyResultActivity.MONKEY_CALLBACK);
                    }
                });
    }

    static private void sendMessage(Handler handler, String result, int what) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = result;
        handler.sendMessage(message);
        Log.d(TAG, "sendMessage: " + result);
    }

}
