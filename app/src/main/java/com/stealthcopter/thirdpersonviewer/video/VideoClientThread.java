package com.stealthcopter.thirdpersonviewer.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.stealthcopter.thirdpersonviewer.VRViewActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import timber.log.Timber;

/**
 * Created by Alvin on 2016-05-20.
 */
public class VideoClientThread implements Runnable {
    private Socket mSocket;
    private Boolean mRunFlag = true;
    private BitmapFactory.Options bitmap_options = new BitmapFactory.Options();

    public VideoClientThread(Socket socket) throws IOException {
        this.mSocket = socket;
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap_options.inMutable = true;
    }

    Bitmap bitmap;

    int frames = 0;
    long prevTime = -1;

    public void stop(){
        mRunFlag = false;
    }


    @Override
    public void run() {
        try {
            InputStream inStream = null;
            try {
                inStream = mSocket.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            DataInputStream is = new DataInputStream(inStream);
            while (mRunFlag) {
                try {
                    int token = is.readInt();
                    if (token == 4) {
                        if (is.readUTF().equals("#@@#")) {

                            int imgLength = is.readInt();

                            is.readUTF();

                            byte[] buffer = new byte[imgLength];
                            int len = 0;
                            while (len < imgLength) {
                                len += is.read(buffer, len, imgLength - len);
                            }

                            if (bitmap != null){
                                bitmap_options.inBitmap = bitmap;
                            }

                            bitmap =  BitmapFactory.decodeByteArray(buffer, 0, buffer.length,bitmap_options);

                            logFrameRate();

                            if (bitmap != null){
                                VRViewActivity.mLastFrame = bitmap;
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

        if (mSocket != null){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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


