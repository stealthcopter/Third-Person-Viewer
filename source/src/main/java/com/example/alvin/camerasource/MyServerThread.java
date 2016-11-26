package com.example.alvin.camerasource;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alvin on 2016-05-20.
 */
public class MyServerThread implements Runnable {
    private int mServerPort;
    private String mServerIP;
    private Context mContext;
    private Handler mHandler;
    private MainActivity mActivityInstance;

    public MyServerThread(Context context, String serverip, int serverport, Handler handler) {
        super();
        mContext = context;
        mServerIP = serverip;
        mServerPort = serverport;
        mHandler = handler;
        mActivityInstance = (MainActivity) mContext;
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
            Log.d("ServerThread", "run: erro");
        }
    }

    public class ServerSocketThread implements Runnable {
        Socket s = null;
        // BufferedReader br = null;
        //BufferedWriter bw = null;
        OutputStream os = null;

        long startTime = -1;
        long timeTaken;

        public ServerSocketThread(Socket s) throws IOException {
            this.s = s;
            //br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        }


        @Override
        public void run() {
            if (s != null) {
                String clientIp = s.getInetAddress().toString().replace("/", "");
                int clientPort = s.getPort();
                System.out.println("====client ip=====" + clientIp);
                System.out.println("====client port=====" + clientPort);
                try {

                    s.setKeepAlive(true);
                    os = s.getOutputStream();
                    while (true) {

                        startTime = System.currentTimeMillis();

                        //服务器端向客户端发送数据
                        //dos.write(mPreview.mFrameBuffer.);
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeInt(4);
                        dos.writeUTF("#@@#");
                        dos.writeInt(mActivityInstance.mPreview.mFrameBuffer.size());
                        dos.writeUTF("-@@-");
                        dos.flush();
                        // System.out.println(mActivityInstance.mPreview.mFrameBuffer.size());
                        dos.write(mActivityInstance.mPreview.mFrameBuffer.toByteArray());
                        //System.out.println("outlength"+mPreview.mFrameBuffer.length);
                        dos.flush();

                        timeTaken = System.currentTimeMillis() - startTime;

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
