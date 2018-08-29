package manacle.selfiecamera;

import android.os.Environment;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by manacl-php on 17/8/18.
 */

public class Constants {

    public static final String KEY_IS_FRONT_CAMERA = "is_camera_front";
    public static final String TEST_LOCATION = Environment.getExternalStorageDirectory() + Constants.FOLDER_NAME;
    public static final String FOLDER_NAME = "/Selfie/DCIM";
    public static final int IMAGE_QUALITY = 50;

    public static final String KEY_PATH_FRONT_IMAGE = "front_image_path";
    public static final String KEY_PATH_REAR_IMAGE = "back_image_path";

    public static String generateUniqueId() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        Log.v("KEY_UNIQUE_ID===>", df.format(c.getTime()));
        return df.format(c.getTime());
    }

}
