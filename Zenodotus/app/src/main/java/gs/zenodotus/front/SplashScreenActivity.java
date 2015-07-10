package gs.zenodotus.front;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import gs.zenodotus.R;
import gs.zenodotus.back.commands.GetCapabilitiesCommand;

public class SplashScreenActivity extends Activity
        implements TaskFragment.TaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        this.deleteDatabase("perseus_capabilities.db");
//        GlobalDataProvider.setCapabilitiesActual(this);
//        Log.v("Splash screen", "Done!");
//        GlobalDataProvider.setOldDate(this);
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT)
                    .commit();
        }
    }

    public void onGetCapabilitiesSuccess() {
        Intent i = new Intent(this, MainDisplayActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onGetCapabilitiesFail(int errno) {
        switch (errno) {
            case GetCapabilitiesCommand.LOST_CONNECTION:
                // TODO add refresh button!
                Log.d("GetcapabilitiesFail", "lost connection");
                break;
            case GetCapabilitiesCommand.BAD_ANSWER:
                // TODO add refresh button and give info!
                Log.d("GetcapabilitiesFail", "bad answer");
                break;
        }
    }
}