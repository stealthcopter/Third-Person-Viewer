package com.stealthcopter.thirdpersonviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import timber.log.Timber;

/**
 * Created by Alvin on 2016-05-20.
 */
public class MyClientThread implements Runnable {
    private Socket mSocket;
    private Handler mHandler;
    private Boolean mRunFlag = true;
    private BitmapFactory.Options bitmap_options = new BitmapFactory.Options();

    public MyClientThread(Socket socket, Handler handler) throws IOException {
        this.mSocket = socket;
        this.mHandler = handler;
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap_options.inMutable = true;
    }

    Bitmap bitmap;

    int frames = 0;
    long prevTime = -1;


    @Override
    public void run() {
        try {
            InputStream inStream = null;
            try {
                inStream = mSocket.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DataInputStream is = new DataInputStream(inStream);
            while (mRunFlag) {
                try {
                    int token = is.readInt();
                    if (token == 4) {
                        if (is.readUTF().equals("#@@#")) {
                            //System.out.println("before-token" + token);
                            int imgLength = is.readInt();
//                            System.out.println("getLength:" + imgLength);
//                            System.out.println("back-token" + is.readUTF());
                            is.readUTF();

                            byte[] buffer = new byte[imgLength];
                            int len = 0;
                            while (len < imgLength) {
                                len += is.read(buffer, len, imgLength - len);
                            }
                            Message m = mHandler.obtainMessage();

                            if (bitmap != null){
                                bitmap_options.inBitmap = bitmap;
                            }

                            bitmap =  BitmapFactory.decodeByteArray(buffer, 0, buffer.length,bitmap_options);

                            logFrameRate();

                            m.obj = bitmap;

                            if (m.obj != null) {
                                mHandler.sendMessage(m);
                            } else {
                                System.out.println("Decode Failed");
                            }
                        }
                    }else{
                        //Log.d(TAG,"Skip Dirty bytes!!!!"+Integer.toString(token));
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void logFrameRate(){
        frames++;

        if (prevTime == - 1){
            prevTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - prevTime > 5000 ){
            Timber.d("Frames per second: "+frames/5.0f);
            frames = 0;
            prevTime = System.currentTimeMillis();
        }
    }
}


