package com.hzp.pedometer.persistance.file;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    private Context context;
    private SimpleDateFormat dateFormat;

    private List<String> dataList;
    private FileOutputStream fileOutputStream;

    private ScheduledExecutorService executorService;
    private long period = 1000;

    private static final String dataPrefix = "data_log";

    public StepDataStorage(Context context) {
        this.context = context;
        dataList = Collections.synchronizedList(
                new LinkedList<String>());
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void startNewRecord() throws FileNotFoundException {
        endRecord();
        //创建并打开记录文件
        fileOutputStream = context.openFileOutput(createFileName(),Context.MODE_APPEND);
        //启动定时线程
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new WriterTask(), period, period, TimeUnit.MILLISECONDS);
    }

    public void saveData(String data) {
        dataList.add(data);
    }

    public void endRecord() {
        if(executorService!=null && !executorService.isShutdown()){
            executorService.shutdownNow();
        }
        if(fileOutputStream!=null){
            closeStream();
        }
    }

    public String[] getDataFileNames(){
        return context.getFilesDir().list(new DataFilenameFilter());
    }

    public void deleteFile(String filename){
        context.deleteFile(filename);
    }

    public void deleteFile(String[] filenames){
        for(String filename:filenames){
            deleteFile(filename);
        }
    }

    private void closeStream(){
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            //TODO 异常处理
            e.printStackTrace();
        }finally {
            fileOutputStream = null;
        }
    }

    class WriterTask implements Runnable{

        @Override
        public void run() {
            try{
                for(int i =0;i<dataList.size();i++){
                    String data = dataList.remove(0);
                    if(fileOutputStream!=null){
                        fileOutputStream.write(data.getBytes());
                    }
                }
            }catch (IndexOutOfBoundsException e){

            } catch (IOException e) {
                //TODO
                e.printStackTrace();
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
