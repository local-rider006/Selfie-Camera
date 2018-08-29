package manacle.selfiecamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private static final int REQUEST_CAMERA_PERMISSION = 141;
    private static final String FRAGMENT_DIALOG = "dialog";
    private Uri front_image_path = null;
    private Uri back_image_path = null;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;
    private boolean isFrontCamera;
    private String page;
    private String unique_id;
    private File _capturedImageFile = null;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraView = findViewById(R.id.camera);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        getSupportActionBar().setTitle("Capture Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        permissionCheck();

        //Getting PutExtra of Front Camera:-
        isFrontCamera = getIntent().getBooleanExtra(Constants.KEY_IS_FRONT_CAMERA, false);

        findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
            }
        });

        initializeCameraCallback();
    }

    /**
     * Comment by Ajay Thakur on 2018/08/20
     * <p>
     * Initialize camera callback methods.
     */
    private void initializeCameraCallback() {
        CameraView.Callback mCallback = new CameraView.Callback() {
            @Override
            public void onCameraOpened(CameraView cameraView) {
                Log.d(TAG, "onCameraOpened");
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                Log.d(TAG, "onCameraClosed");
            }

            @Override
            public void onPictureTaken(CameraView cameraView, final byte[] data) {

                //Generating Unique_id for every time we take a pic using front camera:-
                unique_id = Constants.generateUniqueId();
                Log.d(TAG, "onPictureTaken: captured file length ==> " + data.length);
                getBackgroundHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            generateNewFile(data);
                        } catch (NullPointerException e) {
                            Log.w(TAG, "Cannot write to " + _capturedImageFile, e);
                        }
                    }
                });
            }
        };

        if (mCameraView != null) {
            if (isFrontCamera) {
                mCameraView.setFacing(CameraView.FACING_FRONT);
            } else {
                mCameraView.setFacing(CameraView.FACING_BACK);
            }

            mCameraView.setAutoFocus(true);
            mCameraView.setFlash(CameraView.FLASH_AUTO);
            mCameraView.addCallback(mCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* //Runtime Permission Check :-
        if (ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            TedPermission.with(CameraActivity.this)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            mCameraView.start();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            finish();
                        }
                    })
                    .setGotoSettingButtonText("SETTINGS")
                    .setDeniedMessage("If you reject permission,you can not use this service" +
                            "\n\nPlease turn on permissions at [Settings] > [Permission]")
                    .setPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } else {
            mCameraView.start();
        }*/
    }

    public void permissionCheck(){

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(CameraActivity.this , "Permission Granted !" , Toast.LENGTH_SHORT).show();
                mCameraView.start();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }


    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    /**
     * Comment by Ajay Thakur on 2018/08/20
     * <p>
     * generating a new file in a folder in device directory.
     */
    private void generateNewFile(byte[] _dataBytes) {
        try {
            File _imageFolder = new File(Constants.TEST_LOCATION);

            //Image unique name:-
            _capturedImageFile = new File(_imageFolder.getPath(), "_" + unique_id + ".jpg");

            if (!_imageFolder.exists()) {
                _imageFolder.mkdirs();
                Log.i(TAG, "Camera Directory created");
            }

            if (!_capturedImageFile.exists()) {
                _capturedImageFile.getParentFile().mkdirs();
                _capturedImageFile.createNewFile();
            }

            writeDataToFile(_capturedImageFile, _dataBytes);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comment by Ajay Thakur on 2018/08/20
     * <p>
     * writing byte[] to previously generated file in above method(generateNewFile).
     */
    private void writeDataToFile(File _file, byte[] _dataBytes) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(_file);
            os.write(_dataBytes);
            os.close();

            //Crop Image Method:-
            cropImage(_file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Comment by Ajay Thakur on 2018/08/20
     * <p>
     * cropping image.
     */
    /**
     * Comment by Ajay Thakur on 2018/08/20
     * <p>
     * processing cropped file to generate final required image file and
     * passing intent data to respective activity.
     */
    private void passingImageThroughIntent(Uri fileUri, File mFile) {
        // Get the File path from the Uri
        String path = FileUtils.getPath(CameraActivity.this, fileUri);

        // Alternatively, use FileUtils.getFile(Context, Uri)
        if (path != null) {
            mFile = FileUtils.getFile(CameraActivity.this, fileUri);
        }

        if (mFile != null && mFile.exists()) {
            Uri _capturedFileUri = FileUtils.getUri(mFile);
            Intent intent = new Intent(CameraActivity.this, StillShotActivity.class);
            intent.putExtra(Constants.KEY_IS_FRONT_CAMERA, isFrontCamera);

            if (isFrontCamera) {
                intent.putExtra(Constants.KEY_PATH_FRONT_IMAGE, _capturedFileUri);
                intent.putExtra(Constants.KEY_PATH_REAR_IMAGE, back_image_path);
            } else {
                intent.putExtra(Constants.KEY_PATH_FRONT_IMAGE, front_image_path);
                intent.putExtra(Constants.KEY_PATH_REAR_IMAGE, _capturedFileUri);
            }

            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    passingImageThroughIntent(result.getUri(), _capturedImageFile);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    result.getError().printStackTrace();

                } else {
                    onResume();
                }
                break;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void cropImage(File _file) {
        try {
            if (_file != null && _file.exists()) {
                //Log.e(TAG, "file_size_before_compression(MB): " + Constants.getFileSize(_file));
                Uri _uri = FileUtils.getUri(_file);
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(_uri)
                        .setOutputCompressQuality(Constants.IMAGE_QUALITY)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputUri(_uri)
                        .start(CameraActivity.this);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
