package com.example.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class camara extends AppCompatActivity {
    private static final String TAG = "CameraXBasic";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};


    private ImageCapture imageCapture = null;
    private File outputDirectory;
    private ExecutorService cameraExecutor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        // Set up the listener for take photo button
        Button cameraCaptureButton = findViewById(R.id.camera_capture_button);
        cameraCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        outputDirectory = getOutputDirectory();

        cameraExecutor = Executors.newSingleThreadExecutor();


    }

    private File getOutputDirectory() {
        File[] files = getExternalMediaDirs();
        File mediaDir = null;
        if (files[0] != null) {
            String appname = getResources().getString(R.string.app_name);
            mediaDir = new File(files[0], appname);
            mediaDir.mkdirs();
        }
        if (mediaDir != null && mediaDir.exists())
            return mediaDir;
        else
            return getFilesDir();
    }

    private void takePhoto() {
        ImageCapture imageCapture = this.imageCapture;
        if (imageCapture == null)
            return;

        // Create time-stamped output file to hold the image
        final File photoFile = new File(
                outputDirectory,
                new SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg");

        // Create output options object which contains file + metadata
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(photoFile);
                        String msg = "Photo capture succeeded:" + savedUri;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }

                    @Override
                    public void onError(ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }
                });
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        final camara mainActivity = this; //Pointer to use in inner anonymous class Runnable

        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        // Used to bind the lifecycle of cameras to the lifecycle owner
                        ProcessCameraProvider cameraProvider = null;
                        try {
                            cameraProvider = cameraProviderFuture.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Preview
                        Preview preview = new Preview.Builder().build();
                        PreviewView viewFinder = findViewById(R.id.viewFinder);
                        preview.setSurfaceProvider(viewFinder.createSurfaceProvider());

                        imageCapture = new ImageCapture.Builder().build();

                        // Select back camera as a default
                        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;


                        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder().build();
                        imageAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer());



                        try {
                            // Unbind use cases before rebinding
                            cameraProvider.unbindAll();

                            // Bind use cases to camera
                            cameraProvider.bindToLifecycle(
                                    mainActivity, cameraSelector, preview, imageCapture);

                        } catch (Exception exc) {
                            Log.e(TAG, "Use case binding failed", exc);
                        }

                    }
                }
                , ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        int i = 0;
        while (i < REQUIRED_PERMISSIONS.length
                && ContextCompat.checkSelfPermission(getBaseContext(), REQUIRED_PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED) {
            i++;
        }
        return i == REQUIRED_PERMISSIONS.length;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private class LuminosityAnalyzer implements ImageAnalysis.Analyzer {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void analyze(ImageProxy image) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = byteBuffertoByteArray(buffer);
            int[] pixels = new int[data.length];
            for (int i = 0; i < data.length ; i++) {
                Byte b = data[i];

                pixels[i] = b.intValue() & 0xFF;
            }
            double luma = Arrays.stream(pixels).average().getAsDouble();

            listener(luma);

            image.close();
        }

        private byte[] byteBuffertoByteArray(ByteBuffer buffer) {
            buffer.rewind(); // Rewind the buffer to zero
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data); // Copy the buffer into a byte array
            return data; // Return the byte array
        }

        private void listener(double luma) {
            Log.d(TAG, "Average luminosity: " + luma);
        }
    }
}