package com.example11.zhouh.sensordata_collection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    public String path;
    private Button b_start, b_stop;
    private int UserName=100, GestureType=100, UnexpectedMovement=100, DEFAULT=100;
    public static final String[] UserList = {
            "ZhouH", "ZengSY", "LinYX","ZhangWZ"
    };
    public static final String[] GestureList = {
            "0", "1", "2","3","4","5","6","7","8","9"
    };
    public static final String[] UnexpectedList = {
            "Walk", "Stand up", "Sit down","Turn"
    };
    private WearableRecyclerView mRecyclerView;
    private RecordAdapter mRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(permissions, REQUEST_CODE);
        //view与adapter绑定
        mRecyclerView = (WearableRecyclerView) findViewById(R.id.container);
        mRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setEdgeItemsCenteringEnabled(true);
        mRecordAdapter = new RecordAdapter();
        mRecyclerView.setAdapter(mRecordAdapter);

        while (UserName != DEFAULT && GestureType != DEFAULT && UnexpectedMovement != DEFAULT){
            Log.d(tag, "Please choose ur profile");
        }

        mTextView = (TextView) findViewById(R.id.text);
        b_start = (Button) findViewById(R.id.b_start);
        b_stop = (Button) findViewById(R.id.b_stop);
        //b_quit = (Button)findViewById(R.id.b_quit);
        b_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserName != DEFAULT && GestureType != DEFAULT && UnexpectedMovement != DEFAULT) {
                    buildDir();
                    if (sensorData == null) {
                        sensorData = new SensorData(MainActivity.this);
                        if (sensorData.recording == false) {
                            sensorData.F_init(path);
                            //Log.d(tag, "Finit");
                            Toast.makeText(getApplicationContext(), "Start recording",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (sensorData.recording == false) {
                            buildDir();
                            sensorData.RE_init(path);
                            Log.d(tag, "REinit");
                            Toast.makeText(getApplicationContext(), "Start recording",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Already Startted.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please choose profile first",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        b_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sensorData == null) {
                    Toast.makeText(getApplicationContext(), "Hassn't Startted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (sensorData.recording == true) {
                        sensorData.saveData();
                        Toast.makeText(getApplicationContext(), "Stop recording",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Hassn't Startted",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }
    //根据用户名等建立文件夹
    private void buildDir(){
        path = Environment.getExternalStorageDirectory().toString() + "/SensorData_Collection_Storage/"+ UserList[UserName] + "/" + UnexpectedList[UnexpectedMovement] + "/" + GestureList[GestureType] + "/";
        File destDir = new File(path);
        Log.d(tag, "folder is "+path);
        if (!destDir.exists()) {
            //Log.d(tag, "not exist");
            if (destDir.mkdirs()) {
                Log.d(tag, "folder created");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sensorData != null) {
            sensorData.unregister();
            sensorData = null;
        }
        Log.i(tag, "program quit");
        finish();
        //destroy();//需要在onDestroy方法中进一步检测是否回收资源等。
    }

    private class RecordHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layout;
        private CheckBox checkBox;
        private TextView letters;
        private Button clearButton;

        public RecordHolder(View view, int viewType) {
            super(view);
            if (viewType == 0) {
                layout = (RelativeLayout) view.findViewById(R.id.list_container);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                letters = (TextView) view.findViewById(R.id.text);
                letters.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        int index;
                        if (position > UserList.length + GestureList.length - 1) {
                            index = position - UserList.length - GestureList.length;
//                          Log.d(tag, "pos is "+ String.valueOf(position)+ " and index is "+ String.valueOf(index));
                            UnexpectedMovement = index;
                        }
                        else if (position > UserList.length - 1) {
                            index = position - UserList.length;
                            GestureType = index;
                        }
                        else {
                            UserName = position;
                        }
                        bindItem(position);
                        // TODO 当一个被选中之后，立刻刷新其附近的，避免冲突，结果有问题
//                        for(int i = position-2; i < position + 2; i++) {
//                            if (i > 0 && i < UnexpectedMovement + UserList.length + GestureList.length){
//                                bindItem(i);
//                            }
//                        }
                    }
                });
            } else {
//                layout = (RelativeLayout) view.findViewById(R.id.button_container);
//                clearButton = (Button) view.findViewById(R.id.clear_button);
//                if (viewType == 1) {
//                    clearButton.setText(R.string.action_clear);
//                    clearButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d(tag, "onClick: clear");
//                            for (int i = 0; i < WRITTEN.length; ++i)
//                                WRITTEN[i] = false;
//                        }
//                    });
//                } else {
//                    clearButton.setText(R.string.action_test);
//                    clearButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Log.d(tag, "onClick: test");
//                            Intent intent = RotationMatrixTestActivity.newIntent(MainActivity.this);
//                            startActivity(intent);
//                        }
//                    });
            }
        }

        //判断该position是否对应于一个已经被打勾的类别，
        //判断position属于哪个类别（user,gesture,unexpect)并取得对应字符串
        private void bindItem(int position) {
            //Log.d(tag, "bindItem: " + position);
            checkBox.setClickable(false);
            if (position == UserName || position == GestureType + UserList.length || position == UnexpectedMovement + UserList.length + GestureList.length)
                checkBox.setChecked(true);
            else checkBox.setChecked(false);
            if (position > UserList.length + GestureList.length - 1) {
                position -= UserList.length + GestureList.length;
                letters.setText(UnexpectedList[position]);
            }
            else if (position > UserList.length - 1) {
                position -= UserList.length;
                letters.setText(GestureList[position]);
            }
            else {
                letters.setText(UserList[position]);
            }
        }
    }

    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        @NonNull
        @Override
        public RecordHolder onCreateViewHolder(@NonNull ViewGroup container, int i) {
            Log.d(tag, "create view holder");
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            if (i == 0) {
                View view = inflater.inflate(R.layout.list_item, container, false);
                return new RecordHolder(view, i);
            } else {
                View view = inflater.inflate(R.layout.clear_button, container, false);
                return new RecordHolder(view, i);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecordHolder recordViewHolder, int position) {
            recordViewHolder.bindItem(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (position < UserList.length)
                return 0;
            else if (position < UserList.length + GestureList.length)
                return 0;
            else return 0;
        }

        @Override
        public int getItemCount() {
            return UserList.length + GestureList.length + UnexpectedList.length;
        }
    }
}
//1.数据按所需格式存储,存储位置ok,格式ok,数据格式ok
//2.用户可以输出自己的名字和姿势
//3.点击start stop之后有提示,OK
//4.删除quit,改到OnDestory里面,OK





