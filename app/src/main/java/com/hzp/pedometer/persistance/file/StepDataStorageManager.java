package com.hzp.pedometer.persistance.file;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 何志鹏 on 2016/2/1.
 * @email hoholiday@hotmail.com
 */
public class StepDataStorageManager {

    private static StepDataStorageManager instance;
    private Context context;
    private SimpleDateFormat dateFormat;

    private List<String> dataBuffer;//写入数据的缓存区
    private FileOutputStream fileOutputStream;

    private ScheduledExecutorService executorService;
    private long period = 10000;//缓存写入时间间隔

    private static final String dataPrefix = "data_log";

    private String writingFile;//正在被编辑的文件名

    private StepDataStorageManager() {
    }

    public static StepDataStorageManager getInstance() {
        if (instance == null) {
            synchronized (StepDataStorageManager.class) {
                if (instance == null) {
                    instance = new StepDataStorageManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        dataBuffer = Collections.synchronizedList(
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
            writingFile = createFileName();
            fileOutputStream = context.openFileOutput(writingFile, Context.MODE_APPEND);
            //启动定时线程
            executorService = Executors.newScheduledThreadPool(1);
            //延迟period后启动写入任务，然后每间隔period再进行一次写入
            executorService.scheduleAtFixedRate(new WriterTask(), period, period, TimeUnit.MILLISECONDS);
        } catch (FileNotFoundException |RejectedExecutionException e) {
            e.printStackTrace();
            endRecord();
        }
    }

    public void saveData(String data) {
        dataBuffer.add(data);
    }

    public void endRecord() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        closeStream();
        clearBuffer();
        writingFile = null;
    }

    /**
     * 获得加速度数据存储文件名列表
     *
     * @return 文件名列表
     */
    public String[] getDataFileNames() {
        String[] result = context.getFilesDir().list(new DataFilenameFilter());
        //不读取正在被写入的文件
        if (writingFile != null && result != null) {
            int index = Arrays.binarySearch(result, 0, result.length, writingFile);
            if (index >= 0) {
                System.arraycopy(result, index + 1, result, index, result.length - 1 - index);
                result[result.length - 1] = null;
            }
        }
        return result;
    }

    public long getLastModifyTime(String filename) {
        if (filename != null) {
            File file = new File(context.getFilesDir().getPath() + File.separator + filename);
            return file.lastModified();
        } else {
            return 0;
        }
    }

    public void clearBuffer() {
        dataBuffer.clear();
    }

    private void closeStream() {
        if (fileOutputStream == null) {
            return;
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            //TODO 异常处理
            e.printStackTrace();
        } finally {
            fileOutputStream = null;
        }
    }

    /**
     * 获取加速度存储文件的记录起始时间
     *
     * @param stepDataName 文件名
     * @return 起始时间
     */
    public long getDataStartTime(String stepDataName) {
        long result = 0;
        if (stepDataName == null) {
            return result;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(
                            context.getFilesDir().getPath() + File.separator + stepDataName));
            if (reader.ready()) {
                //起始时间保存在第一行
                result = Long.valueOf(reader.readLine());
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }

    class WriterTask implements Runnable {

        @Override
        public void run() {
            try {
                for (int i = 0; i < dataBuffer.size(); i++) {
                    String data = dataBuffer.remove(0);
                    if (fileOutputStream != null) {
                        fileOutputStream.write(data.getBytes());
                    }
                }
            } catch (IndexOutOfBoundsException | IOException e) {
                endRecord();
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
