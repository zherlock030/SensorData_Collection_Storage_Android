/*
package com.example11.zhouh.sensordata_collection;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

// Created by Echo on 2018/6/15.

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    public int value;
    private String ip;
    private int port;
    private Socket sck;
    private boolean stopThread;
    private String tag = "Client_Thred";

    public ClientThread(){
        ip = "192.168.199.186";//"192.168.199.186";//"10.214.149.164";//"192.168.1.186";//PCLEDE:192.168.1.191   EmNets301: 192.168.1.186
        port = 10000;
        stopThread = false;
        value = 0;
    }



    @Override
    public void run() {
        try {
            //Log.i(tag,"socket-- create");
            sck = new Socket(ip, port);
            Log.i(tag,"socket create");
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(!stopThread){
            if(Global.q.size() > 0){
                //Log.i(tag,"data_read");
                Global.lock.lock();
                //String content = Global.q.remove();
                String content = "";
                //content = Global.q.remove();//***似乎多读几个也可以
                for(int i=0; i< Global.q.size(); i++  ){
                    content = content.concat(Global.q.remove());
                    //Log.i(tag, Integer.toString(i));
                    //Log.i(tag, content);
                }
                Global.lock.unlock();
                byte[] bstream = content.getBytes();
                //content = "";
                try {
                    OutputStream os = sck.getOutputStream();
                    os.write(bstream,0,bstream.length);
                    //Log.i(tag,"send data");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void stopThread(){
        stopThread = true;
        try{
            sck.getOutputStream().flush();  //这里要一次性flush全部。
            sck.close();
            value = 0;
            Log.i(tag,"close-sck");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
*/