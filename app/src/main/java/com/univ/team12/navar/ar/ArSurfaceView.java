package com.univ.team12.navar.ar;

/**
 * Created by Amal Krishnan on 27-01-2017.
 */

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.beyondar.android.util.Logger;

import java.io.IOException;

/** A basic Camera preview class */
public class ArSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean mIsPreviewing;

    private static final String TAG="CamSurfaceView";

//    public ArSurfaceView(Context context) {
//        super(context);
//        this.init(context);
//    }
//
//    private void init(Context context) {
//        this.mIsPreviewing = false;
//        this.mHolder = this.getHolder();
//        this.mHolder.addCallback(this);
//        this.configureCamera();
//    }
//
//    public boolean isPreviewing() {
//        return this.mCamera != null && this.mIsPreviewing;
//    }
//
//    private void configureCamera() {
//        if(this.mCamera == null) {
//            try {
//                Logger.v("camera", "getTheCamera");
//                boolean acquiredCam = false;
//                int timePassed = 0;
//
//                while(!acquiredCam && timePassed < 1000) {
//                    try {
//                        this.mCamera = Camera.open();
//                        Logger.v("camera", "acquired the camera");
//                    } catch (Exception var5) {
//                        Logger.e("camera", "Exception encountered opening camera:" + var5.getLocalizedMessage());
//
//                        try {
//                            Thread.sleep(200L);
//                        } catch (InterruptedException var4) {
//                            Logger.e("camera", "Exception encountered sleeping:" + var4.getLocalizedMessage());
//                        }
//                        timePassed += 200;
//                    }
//                }
//            } catch (Exception var2) {
//                Logger.e("camera", "ERROR: Unable to open the camera", var2);
//                return;
//            }
//        }
//    }

    public ArSurfaceView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void startPreviewCamera() {
//        if(this.mCamera == null) {
//            this.init(this.getContext());
//        }

        if(mCamera != null && !this.mIsPreviewing) {
            this.mIsPreviewing = true;

            try {
                mCamera.setPreviewDisplay(this.mHolder);
                mCamera.startPreview();
            } catch (Exception var2) {
                Logger.w("camera", "Cannot start preview.", var2);
                this.mIsPreviewing = false;
            }

        }
    }

    public void releaseCamera() {
        this.stopPreviewCamera();
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    }

    public void stopPreviewCamera() {
        if(mCamera != null && this.mIsPreviewing) {
            mIsPreviewing = false;
            mCamera.stopPreview();
        }
    }

}