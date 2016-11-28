package com.stealthcopter.thirdpersonviewer.view;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

import timber.log.Timber;

/**
 * Created by Alvin on 2016-05-20.
 */
public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public ByteArrayOutputStream mFrameBuffer;

    /**
     * Constructor of the MyCameraView
     *
     * @param context
     * @param camera
     */
    public CameraPreviewView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    /**
     * set preview to the camera
     *
     * @param holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * surface destroyed function
     *
     * @param holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    int width = 1280;
    int height = 720;
//
//    int width = 960;
//    int height = 540;

    /**
     * surface changed function
     *
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            //Configration Camera Parameter(full-size)
            Camera.Parameters parameters = mCamera.getParameters();

            parameters.setPreviewSize(width, height);
            //this.width = parameters.getPreviewSize().width;
            //this.height = parameters.getPreviewSize().height;
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(parameters);
            // mCamera.setDisplayOrientation(90);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * frame call back function
     *
     * @param data
     * @param camera
     */
    YuvImage yuvimage;
    ByteArrayOutputStream baos;
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {

            oldWorkingMethod(data);

            if (baos != null){
                mFrameBuffer = baos;
            }

        } catch (Exception e) {
            Timber.e(e, "error");
        }
    }

    public void oldWorkingMethod(byte[] data){

        yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 40, baos);

    }

}

