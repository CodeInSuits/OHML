package com.example.bchoi.ohms;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bchoi on 8/23/15.
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    TextView testView;
    ImageView overlay;
    int overlay_counter;
    static int REQUEST_CODE;
    boolean proceed_next;
    boolean damanged = false;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback rawCallBack;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        overlay = (ImageView) findViewById(R.id.overlay);
        overlay.setImageResource(R.drawable.template_fl);
        overlay_counter=0;
        //Log.d("#####################", "autocapture from onCreate");
        autoCapture();

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(String.format("/sdcard/ohms/abc.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                //Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG).show();
                //swapOverlay();
                goToFileLoader();
                refreshCamera();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("*********************", "autocapture from Result");
        autoCapture();
        if((requestCode == REQUEST_CODE) && (resultCode == RESULT_OK)) {
            proceed_next = data.getExtras().getBoolean("proceed_next");
            //Log.d("@@@@@@@@@@@@@@@@@", "value of proceed_next: "+proceed_next);
            if(proceed_next) {
                swapOverlay();
            }
        }
    }

    public void goToFileLoader() {
        Intent intent = new Intent(this, FileLoader.class);
        //intent.putExtra("proceed_next", proceed_next);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void autoCapture() {
        Handler handler = new Handler();

        if (damanged == false) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    camera.takePicture(null, null, jpegCallback);
                }
            }, 10000);
        }

        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    camera.takePicture(null, null, jpegCallback);
                }
            }, 30000);
            damanged = false;
        }
    }

/*
    public void captureImage(View v) throws IOException {
        // take the picture
        camera.takePicture(null, null, jpegCallback);
    }
    */

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore
        }

        // set preview size and make any resize, rotate  or
        // reforming changes here
        // start preview with new changes
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch(Exception e) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
            camera.setDisplayOrientation(90);
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters parameters = camera.getParameters();


        // You need to choose the most appropriate previewSize for your app
        Camera.Size previewSize = parameters.getPreviewSize();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        camera.setParameters(parameters);

        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    public void swapOverlay () {
        ImageView overlay;
        overlay = (ImageView) findViewById(R.id.overlay);

        if(overlay_counter == 0) {
            overlay.setImageResource(R.drawable.template_fr);
            overlay_counter++;
        }

        else if(overlay_counter == 1) {
            overlay.setImageResource(R.drawable.template_br);
            overlay_counter++;
        }

        else if(overlay_counter == 2) {
            overlay.setImageResource(R.drawable.template_bl);
            overlay_counter++;
            damanged = true;
        }

        else if(overlay_counter == 3) {
            overlay.setImageResource(R.drawable.template_fl);
            startActivity(new Intent(getApplicationContext(), MaacoActivity.class));
            overlay.setImageResource(R.drawable.template_front);
            overlay_counter++;
        }

        else if(overlay_counter == 4) {
            overlay.setImageResource(R.drawable.template_side);
            damanged = true;
            overlay_counter++;
        }

        else {
            //Toast.makeText(getApplicationContext(), "DONE!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), FinishActivity.class));
            finish();
        }
    }
}
