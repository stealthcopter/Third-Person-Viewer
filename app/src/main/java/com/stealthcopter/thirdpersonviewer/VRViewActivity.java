package com.stealthcopter.thirdpersonviewer;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.stealthcopter.thirdpersonviewer.video.VideoClientThread;

import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VRViewActivity extends AppCompatActivity {
    @BindView(R.id.camera_preview) ImageView mCameraView;
    @BindView(R.id.camera_preview2) ImageView mCameraView2;

    public static String SERVERIP = "192.168.43.203";
    public VideoClientThread mClient;
    public static Bitmap mLastFrame;

    private final Handler handler = new Handler();

    private Runnable updateImageRunnable = new Runnable() {
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
                handler.postDelayed(updateImageRunnable, 1000 / 30);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateImageRunnable);
        startSocket();
    }

    private void hideDecorView(){
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void startSocket() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... unused) {
                // Background Code
                Socket s = null;
                try {
                    s = new Socket(SERVERIP, Const.SERVER_PORT);
                    mClient = new VideoClientThread(s);
                    new Thread(mClient).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    private void stopSocket(){
        if (mClient!=null) mClient.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateImageRunnable);
        stopSocket();
    }

}
