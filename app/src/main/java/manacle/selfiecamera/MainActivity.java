package manacle.selfiecamera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button front_camera_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        front_camera_button = findViewById(R.id.front_camera_button);

        front_camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this , CameraActivity.class);
                intent.putExtra(Constants.KEY_IS_FRONT_CAMERA, true);
                startActivity(intent);
            }
        });
    }
}
