package com.squarecircle.automonkeytest.Base;

import android.app.Application;
import android.content.Context;

/**
 * Created by cfm on 2017/7/1.
 */

public class App extends Application {
    
    public static Context context = null;
    
    public static Context getContext() {
        return context;
    }
    
    public App() {
        if (context == null) {
            context = this;
        }
    }
}
