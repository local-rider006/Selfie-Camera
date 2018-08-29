package manacle.selfiecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Ajay Thakur on 20/08/2018.
 */

public class ImageSetActivity  extends AppCompatActivity {
    ImageView iv_image_icon;
    private Uri front_image_path;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_set_layout);
        front_image_path = getIntent().getParcelableExtra(Constants.KEY_PATH_FRONT_IMAGE);
        iv_image_icon = findViewById(R.id.iv_image_icon);

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), front_image_path);
            bitmap = ImageHelper.rotateImageIfRequired(bitmap, front_image_path);
            bitmap = ImageHelper.getResizedBitmap(bitmap, width);
            iv_image_icon.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
