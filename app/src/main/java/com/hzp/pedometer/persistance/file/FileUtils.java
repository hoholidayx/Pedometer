package com.hzp.pedometer.persistance.file;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * @author hoholiday on 2016/4/28.
 * @email hoholiday@hotmail.com
 */
public class FileUtils {
    public static void deleteFile(Context context, String filename) {
        if (filename != null) {
            context.deleteFile(filename);
        }
    }

    public static void deleteFile(Context context, String[] filenames) {
        if (filenames != null) {
            for (String filename : filenames) {
                deleteFile(context, filename);
            }
        }
    }

}
