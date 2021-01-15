package com.example11.zhouh.sensordata_collection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private static final String[] permissions = new String[]{
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static int REQUEST_CODE = 1;
    public SensorData sensorData;
    private String tag = "MainActivity";
    private Button b_start,b_stop,b_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_CODE);
        File destDir = new File(Environment.getExternalStorageDirectory().toString() + "/SensorData_Collection_Storage");
        Log.d(tag, Environment.getExternalStorageDirectory().toString() + "/SensorData_Collection_Storage");
        if (!destDir.exists()) {
            Log.d(tag, "not exist");
            if (destDir.mkdirs()) {
                Log.d(tag, "sensor folder created");
            }
        }

        for (int i = 0; i < 1; ++i) {
            File subDir = new File(Environment.getExternalStorageDirectory().toString() + "/SensorData_Collection_Storage/ZH_UP/");
            if (!subDir.exists()) {
                if (subDir.mkdirs()) {
                    Log.d(tag, "subdir " + i + " created");
                }
            }
        }
        mTextView = (TextView) findViewById(R.id.text);
        b_start = (Button)findViewById(R.id.b_start);
        b_stop = (Button)findViewById(R.id.b_stop);
        b_quit = (Button)findViewById(R.id.b_quit);
        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sensorData == null) {
                    Log.d(tag, "null true");
                    sensorData = new SensorData(MainActivity.this);
                    if (sensorData.recording == false) {
                        sensorData.F_init();
                        Log.d(tag, "Finit");
                    }
                }
                else{
                    if (sensorData.recording == false) {
                        sensorData.RE_init();
                        Log.d(tag, "REinit");
                    }
                    }
                }
        });
        b_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sensorData.recording == true) {
                    sensorData.saveData();
                }
                else{
                    Log.d(tag, "placeholder");
                }
            }
        });

        b_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sensorData.unregister();
                    sensorData = null;
                    Log.i(tag, "program quit");
                    finish();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //destroy();//需要在onDestroy方法中进一步检测是否回收资源等。
    }
}


//1.数据按所需格式存储,存储位置ok,格式ok,数据格式ok
//2.用户可以输出自己的名字和姿势
//3.点击start stop之后有提示





