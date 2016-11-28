package com.stealthcopter.thirdpersonviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by mat on 27/11/16.
 */

public class SelectActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.startVR) void startVR(){
        startActivity(VRViewActivity.createLink(this, "192.168.43.203"));
    }

    @OnClick(R.id.startServer) void startServer(){
        Timber.e("Start server");
        if (hasPermission()) {
            startActivity(new Intent(this, ServerActivity.class));
        }
        else{
            requestPermission();
        }
    }

    private boolean hasPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA
                },
                PERMISSIONS_REQUEST_PERMISSIONS);
    }

}