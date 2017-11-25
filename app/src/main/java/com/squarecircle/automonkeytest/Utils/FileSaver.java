package com.squarecircle.automonkeytest.Utils;

import android.content.Context;

import com.squarecircle.automonkeytest.Base.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mufengjun260 on 17-11-21.
 */

public class FileSaver {
    public String saveToFile(String fileName, String content) throws IOException {
        Context context = App.getContext();
        String path = context.getFilesDir().getAbsolutePath();
        path += '/' + fileName;
        File file = new File(path);
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(content.getBytes());
        outStream.close();
        return path;
    }
}
