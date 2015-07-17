package gs.zenodotus.front;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gs.zenodotus.R;
import gs.zenodotus.back.GlobalDataProvider;

public class SplashScreenActivity extends Activity
        implements TaskFragment.TaskCallbacks {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private TaskFragment mTaskFragment;

    public void doCommand(View view) {
        hideErrorButton();
        GlobalDataProvider.setOldDate(this);
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT)
                    .commit();
        } else {
            mTaskFragment.retry();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        doCommand(null);
    }

    public void onGetCapabilitiesSuccess() {
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment != null) {
            mTaskFragment.stopExecuting();
        }
        Intent i = new Intent(this, MainDisplayActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onGetCapabilitiesFail(int errno) {
        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment != null) {
            mTaskFragment.stopExecuting();
        }
        showErrorButton();
    }

    private void showErrorButton() {
        TextView textView = (TextView) findViewById(R.id.loading_note_text);
        textView.setVisibility(View.GONE);
        LinearLayout linearLayout =
                (LinearLayout) findViewById(R.id.error_note_elements);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorButton() {
        LinearLayout linearLayout =
                (LinearLayout) findViewById(R.id.error_note_elements);
        linearLayout.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.loading_note_text);
        textView.setVisibility(View.VISIBLE);
    }
}