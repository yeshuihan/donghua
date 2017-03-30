package android.com.powersaver;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    PowerView powerView;
    Button mb;
    Button mbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
         mb= (Button) findViewById(R.id.button);
        mbs= (Button) findViewById(R.id.buttonshow);
        Button setLevel= (Button) findViewById(R.id.setLevel);
        powerView= (PowerView) findViewById(R.id.powerView);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerView.startAnimation();
            }
        });
        mbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerView.showWave();
                powerView.showWaveAnimation();
            }
        });
        setLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerView.setWaveLevelLine(50);
            }
        });
    }

}
