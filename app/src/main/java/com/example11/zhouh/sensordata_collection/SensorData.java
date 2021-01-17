package com.example11.zhouh.sensordata_collection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SensorData {
    private static final String TAG = "SensorData";
    private static SensorData sSensorData;
    private String saveDir;
    public SensorManager sm;
    public Sensor acc, mag, gra, gyro, linacc;
    public boolean recording = false, value = false,FileExist = true;
    private int index;
    private double[] std;
    private File file;
    // 要申请的权限
    public String send;
    private Queue<String> q = new LinkedList<String>();
    private Queue<double[]> gx_q = new LinkedList<double[]>();


    //注册传感器
    public SensorData(Context context) {
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        linacc = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //gra = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void F_init(String path) {
        saveDir = path;
        Log.d(TAG, saveDir);
        index = 0;
        while(FileExist){
            index += 1;
            File destFile = new File(saveDir + String.valueOf(index) + ".txt");
            if (!destFile.exists())
                FileExist = false;
        }
        register();
        recording = true;
        value = true;
    }

    public void RE_init(String path) {
        saveDir = path;
        index = 0;FileExist = true;
        while(FileExist){
            index += 1;
            File destFile = new File(saveDir + String.valueOf(index) + ".txt");
            if (!destFile.exists())
                FileExist = false;
        }
        recording = true;
    }

    private void init() {
        if (std == null)
            std = new double[3];
        Arrays.fill(std, 0);
        recording = false;
        q.clear();
        gx_q.clear();
    }

    private void register() {
        //sm.registerListener(listener,gra,SensorManager.SENSOR_DELAY_FASTEST);
        //sm.registerListener(listener,mag,SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(listener, acc, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(listener, gyro, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(listener, linacc, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        //Log.d(TAG, "unregister: " + q.size());
        //saveData();
        sm.unregisterListener(listener);
    }

    public void saveData() {
        try {
            recording = false;
//          Log.d(HINT, String.valueOf(letters.length));
            Log.d(TAG, "writing file");
            String fileName = saveDir + index + ".txt";
            Log.d(TAG, fileName);

            FileWriter fileWriter = new FileWriter(fileName);
            while (q.size() > 0) {
                fileWriter.write(q.remove());
            }
            fileWriter.close();
            Log.d(TAG, "Sensor" + index + ".txt " + "file saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }


    //public void unregistlistener(){
    //    sm.unregisterListener(listener);
    //}

    //android:background="#FFFFFF"TODO,设置屏幕背景为白色的

    private SensorEventListener listener = new SensorEventListener() {
        float magvalue[] = new float[3];
        float gravalue[] = new float[3];
        float linaccvalue[] = new float[3];
        float accvalue[] = new float[3];
        float gyrovalue[] = new float[3];

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER: {
                    accvalue = sensorEvent.values.clone();
                    break;
                }
                case Sensor.TYPE_LINEAR_ACCELERATION: {
                    linaccvalue = sensorEvent.values.clone();
                    break;
                }
                case Sensor.TYPE_GYROSCOPE: {
                    gyrovalue = sensorEvent.values.clone();
                    send = Float.toString(accvalue[0]) + "   " + Float.toString(accvalue[1]) + "   " + Float.toString(accvalue[2])
                            + "   " + Float.toString(gyrovalue[0]) + "   " + Float.toString(gyrovalue[1]) + "   " + Float.toString(gyrovalue[2])
                            + "   " + Float.toString(linaccvalue[0]) + "   " + Float.toString(linaccvalue[1]) + "   " + Float.toString(linaccvalue[2])
                            + "\r\n";//JAVA代码换行这么随意的
                    //Log.i(TAG,"send = " + send);
                    //Global.lock.lock();
                    if (recording == true) {
                        q.add(send);
                    }
                    break;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    public void getMilliTime(String tag, String msg) {
        long timeStamp = System.currentTimeMillis();
        Log.i(tag, msg + " = " + timeStamp);
    }

    public void getNanoTime(String tag, String msg) {
        long timeStamp = System.nanoTime();
        Log.i(tag, msg + " = " + timeStamp);
    }
}


