package tv.superawesome.sabumperdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tv.superawesome.lib.sabumperpage.SABumperPage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SABumperPage.overrideName("My app");
        SABumperPage.overrideLogo(getResources().getDrawable(R.drawable.kws_white_700));
        SABumperPage.setListener(new SABumperPage.Interface() {
            @Override
            public void didEndBumper() {
                Log.d("SuperAwesome", "Did close");
            }
        });
        SABumperPage.play(this);
    }
}
