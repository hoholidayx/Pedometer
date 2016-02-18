package com.hzp.pedometer.utils;

import android.content.Context;

import java.io.File;

/**
 * @author 何志鹏 on 2016/2/18.
 * @email hoholiday@hotmail.com
 */
public class FileUtils {
    public static long getFileLastModified(Context context, String filename) {
        return new File(context.getFilesDir().getPath() + File.separator + filename)
                .lastModified();
    }
}
