package com.stealthcopter.thirdpersonviewer;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private ImageView mCameraView;
    private ImageView mCameraView2;

    public static String SERVERIP = "192.168.43.203";
    public static final int SERVERPORT = 9191;
    public MyClientThread mClient;
    public Bitmap mLastFrame;

    private final Handler handler = new MyHandler(this);

    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mLastFrame != null) {
                            mCameraView.setImageBitmap(mLastFrame);
                            mCameraView2.setImageBitmap(mLastFrame);
                        }

                    }
                }); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(mStatusChecker, 1000 / 30);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCameraView = (ImageView) findViewById(R.id.camera_preview);
        mCameraView2 = (ImageView) findViewById(R.id.camera_preview2);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... unused) {
                // Background Code
                Socket s;
                try {
                    s = new Socket(SERVERIP, SERVERPORT);
                    mClient = new MyClientThread(s, handler);
                    new Thread(mClient).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
        mStatusChecker.run();
    }

    @Override
    public void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

}
