package manacle.selfiecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Ajay Thakur on 2018/08/20.
 */

public class StillShotActivity extends AppCompatActivity implements View.OnClickListener {
    private Uri front_image_path;
    private ImageView iv_imageView;
    private boolean isFrontCamera;
    private Button next_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stillshot);

        front_image_path = getIntent().getParcelableExtra(Constants.KEY_PATH_FRONT_IMAGE);
        isFrontCamera = getIntent().getBooleanExtra(Constants.KEY_IS_FRONT_CAMERA, false);

        getSupportActionBar().setTitle("Image Preview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
    }

    //initialize control.....
    public void initialize() {
        iv_imageView = findViewById(R.id.iv_stillShot);
        next_button = findViewById(R.id.confirm);

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            Bitmap bitmap = null;
            if (isFrontCamera) {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), front_image_path);
                bitmap = ImageHelper.rotateImageIfRequired(bitmap, front_image_path);
            } else {
                Toast.makeText(StillShotActivity.this, "You don't have a front image path !", Toast.LENGTH_SHORT).show();
            }

            //Resizing Image and Setting on ImageView:-
            bitmap = ImageHelper.getResizedBitmap(bitmap, width);
            iv_imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Next Button OnCLick Mapping:-
        next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                Intent intent = new Intent(StillShotActivity.this, ImageSetActivity.class);
                intent.putExtra(Constants.KEY_PATH_FRONT_IMAGE, front_image_path);

                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //OnBack Pressed
    @Override
    public void onBackPressed() {
        finish();
    }
}

