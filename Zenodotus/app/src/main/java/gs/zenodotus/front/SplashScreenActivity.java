package gs.zenodotus.front;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import gs.zenodotus.R;
import gs.zenodotus.back.GetCapabilitiesCommand;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        this.deleteDatabase("perseus_capabilities.db");
//        Log.v("Splash screen", "Done!");
        GetCapabilitiesCommand command = new GetCapabilitiesCommand(this);
        command.execute();
    }

    public void onGetCapabilitiesSuccess() {
        Intent i = new Intent(this, MainDisplayActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }

}