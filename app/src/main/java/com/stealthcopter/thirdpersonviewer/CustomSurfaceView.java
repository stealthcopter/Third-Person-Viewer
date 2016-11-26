package com.stealthcopter.thirdpersonviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import timber.log.Timber;

/**
 * Created by mat on 25/11/16.
 */

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private int width;
    private int height;

    public CustomSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        this.width = width;
        this.height = height;


        setMeasuredDimension(width, height);
    }


    Rect source = new Rect();
    Rect dst1 = new Rect();
    Rect dst2 = new Rect();

    private void doDraw(Canvas canvas) {

        if (MainActivity.mLastFrame != null) {

            source.set(0, 0, MainActivity.mLastFrame.getWidth(), MainActivity.mLastFrame.getHeight());
            dst1.set(0, 0, width / 2, height);
            dst2.set(width / 2, 0, width, height);

            canvas.drawBitmap(MainActivity.mLastFrame, source, dst1, null);
            canvas.drawBitmap(MainActivity.mLastFrame, source, dst2, null);
        }
        else{
            Timber.e("Frames is null");
        }

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        startGame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopGame();
    }

    private GameThread thread = null;

    public void startGame() {
        Timber.e("startGame...");

        if (thread == null) {
            thread = new GameThread(this);
            thread.startThread();
        }
    }


    public void stopGame() {
        Timber.e("stopGame...");
        if (thread != null) {
            thread.stopThread();

            // Waiting for the thread to die by calling thread.join,
            // repeatedly if necessary
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
            thread = null;
        }
    }

    class GameThread extends Thread {

        private boolean running = false;
        private CustomSurfaceView customSurfaceView = null;
        private SurfaceHolder surfaceHolder = null;
        private int lastFrameNo = -1;

        public GameThread(CustomSurfaceView customSurfaceView) {
            super();
            this.customSurfaceView = customSurfaceView;
            this.surfaceHolder = customSurfaceView.getHolder();
        }

        public void startThread() {
            running = true;
            super.start();
        }

        public void stopThread() {
            running = false;
        }

        public void run() {
            Canvas canvas;
            while (running) {

                synchronized (surfaceHolder) {

                    if (MainActivity.frameNumber != lastFrameNo) {

                        lastFrameNo = MainActivity.frameNumber;

                        canvas = surfaceHolder.lockCanvas();

                        if (canvas != null) {
                            customSurfaceView.doDraw(canvas);

                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }

            }

        }

    }

}
