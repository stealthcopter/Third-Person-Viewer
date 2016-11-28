package com.stealthcopter.thirdpersonviewer.video;

import android.content.Context;
import android.os.Handler;

import com.stealthcopter.thirdpersonviewer.ServerActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import timber.log.Timber;

/**
 * Created by Alvin on 2016-05-20.
 */
public class VideoServerThread implements Runnable {
    private int mServerPort;
    private String mServerIP;
    private Handler mHandler;
    private ServerActivity mActivityInstance;
    private boolean keepRunning;

    public VideoServerThread(Context context, String serverip, int serverport, Handler handler) {
        super();
        mServerIP = serverip;
        mServerPort = serverport;
        mHandler = handler;
        mActivityInstance = (ServerActivity) context;
    }

    public void stop(){
        keepRunning = false;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(mServerPort);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mActivityInstance.serverStatus.setText("Listening on IP: " + mServerIP);
                }
            });
            while (true) {
                Socket s = ss.accept();
                //socketList.add(ss);
                new Thread(new ServerSocketThread(s)).start();
            }
        } catch (Exception e) {
            Timber.e(e, "run: erro");
        }
    }

    public class ServerSocketThread implements Runnable {
        Socket s = null;

        OutputStream os = null;

        long startTime = -1;
        long timeTaken;

        public ServerSocketThread(Socket s) throws IOException {
            this.s = s;
        }


        @Override
        public void run() {
            if (s != null) {
                String clientIp = s.getInetAddress().toString().replace("/", "");
                int clientPort = s.getPort();
                System.out.println("====client ip=====" + clientIp);
                System.out.println("====client port=====" + clientPort);
                try {

                    keepRunning = true;

                    s.setKeepAlive(true);
                    os = s.getOutputStream();
                    while (keepRunning) {

                        startTime = System.currentTimeMillis();

                        //服务器端向客户端发送数据
                        //dos.write(mPreview.mFrameBuffer.);
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeInt(4);
                        dos.writeUTF("#@@#");
                        dos.writeInt(mActivityInstance.mPreview.mFrameBuffer.size());
                        dos.writeUTF("-@@-");
                        dos.flush();

                        dos.write(mActivityInstance.mPreview.mFrameBuffer.toByteArray());

                        dos.flush();

                        timeTaken = System.currentTimeMillis() - startTime;

                        // If we'ere super fast at this, then we should limit the rate to 30fps
                        // Otherwise just operate at device maximum.
                        if (timeTaken < 30){
                            Thread.sleep(30 - timeTaken);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        if (os != null)
                            os.close();

                    } catch (Exception e2) {
                        e.printStackTrace();
                    }

                }


            } else {
                System.out.println("socket is null");
            }
        }

    }
}
