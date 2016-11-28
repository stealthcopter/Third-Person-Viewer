package com.stealthcopter.thirdpersonviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by mat on 27/11/16.
 */

public class SelectActivity extends AppCompatActivity {

    @BindView(R.id.startVR)
    Button startVr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.startVR) void startVR(){
        Timber.e("Start VR");
        startActivity(new Intent(this, VRViewActivity.class));
    }

    @OnClick(R.id.startServer) void startServer(){
        Timber.e("Start server");
        if (hasPermissions()) {
            startActivity(new Intent(this, ServerActivity.class));
        }
        else{
            // TODO: Get permission
        }
    }

    private boolean hasPermissions(){
        return true;
    }

}