package com.hzp.pedometer.persistance.file;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 何志鹏 on 2016/2/1.
 * @email hoholiday@hotmail.com
 */
public class StepDataStorage {

    private static StepDataStorage instance;
    private Context context;
    private SimpleDateFormat dateFormat;

    private List<String> dataList;
    private FileOutputStream fileOutputStream;

    private ScheduledExecutorService executorService;
    private long period = 1000;

    private static final String dataPrefix = "data_log";

    private StepDataStorage() {
    }

    public static StepDataStorage getInstance() {
        if (instance == null) {
            synchronized (StepDataStorage.class) {
                if (instance == null) {
                    instance = new StepDataStorage();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        dataList = Collections.synchronizedList(
                new LinkedList<String>());
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void startNewRecord() {
        endRecord();
        //创建并打开记录文件
        try {
            fileOutputStream = context.openFileOutput(createFileName(), Context.MODE_APPEND);
            //启动定时线程
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(new WriterTask(), period, period, TimeUnit.MILLISECONDS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO: 2016/2/9 异常处理
        }
    }

    public void saveData(String data) {
        dataList.add(data);
    }

    public void endRecord() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (fileOutputStream != null) {
            closeStream();
        }
    }

    public String[] getDataFileNames() {
        return context.getFilesDir().list(new DataFilenameFilter());
    }

    public void deleteFile(String filename) {
        context.deleteFile(filename);
    }

    public void deleteFile(String[] filenames) {
        for (String filename : filenames) {
            deleteFile(filename);
        }
    }

    public void clearBuffer(){
        dataList.clear();
    }

    private void closeStream() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            //TODO 异常处理
            e.printStackTrace();
        } finally {
            fileOutputStream = null;
        }
    }

    public long getDataStartTime(String stepDataName){
        long result = 0;
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            context.getFilesDir().getPath() + File.separator + stepDataName));
            if(reader.ready()){
                result = Long.valueOf(reader.readLine());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    class WriterTask implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                try {
                    for (int i = 0; i < dataList.size(); i++) {
                        String data = dataList.remove(0);
                        if (fileOutputStream != null) {
                            fileOutputStream.write(data.getBytes());
                        }
                    }
                } catch (IndexOutOfBoundsException | IOException e) {
                    endRecord();
                } finally {
                    notifyAll();
                }
            }
        }
    }


    class DataFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.contains(dataPrefix);
        }
    }


    /**
     * 按格式生成一个文件名
     *
     * @return 文件名
     */
    private String createFileName() {
        return dataPrefix + dateFormat.format(Calendar.getInstance().getTime()) + ".txt";
    }

}
