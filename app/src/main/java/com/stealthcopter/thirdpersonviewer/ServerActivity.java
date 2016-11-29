package com.stealthcopter.thirdpersonviewer;

import android.app.ActionBar;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stealthcopter.thirdpersonviewer.network.Networking;
import com.stealthcopter.thirdpersonviewer.video.VideoServerThread;
import com.stealthcopter.thirdpersonviewer.view.CameraPreviewView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import butterknife.BindView;
import butterknife.OnClick;

public class ServerActivity extends AppCompatActivity {
    private Camera mCamera;
    public CameraPreviewView mPreview;
    public static String SERVERIP = "localhost";
    private Handler handler = new Handler();
    private VideoServerThread videoServerThread;

    @BindView(R.id.textView) public TextView serverStatus;
    @BindView(R.id.camera_preview) public FrameLayout camera_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_server);
    }

    @OnClick(R.id.stopButton) void onStopButtonClicked() {
        stopSocket();
        finish();
    }

    /**
     * Get camera instance
     *
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }


    @Override
    public void onResume() {
        super.onResume();

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

        startSocket();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSocket();
    }

    private void startSocket(){
        SERVERIP = Networking.getLocalIpAddress();
        mCamera = getCameraInstance();
        mPreview = new CameraPreviewView(this, mCamera);
        camera_preview.addView(mPreview);

        videoServerThread = new VideoServerThread(this, SERVERIP, Const.SERVER_PORT, handler);

        Thread cThread = new Thread(videoServerThread);
        cThread.start();
    }

    private void stopSocket() {
        if (videoServerThread != null){
            videoServerThread.stop();
        }
    }


}
