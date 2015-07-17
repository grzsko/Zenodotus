package gs.zenodotus.front;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import gs.zenodotus.back.commands.GetCapabilitiesCommand;

/**
 * This Fragment manages a single background task (getting capabiltieis) and
 * retains
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {

    private TaskCallbacks hostActivity;
    private GetCapabilitiesCommand command;
    private boolean executing = false;

    public TaskCallbacks getTaskCallbacks() {
        return hostActivity;
    }

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        hostActivity = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        command = new GetCapabilitiesCommand(this);
        executing = true;
        command.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = null;
    }

    public void retry() {
        if (!executing) {
            command = new GetCapabilitiesCommand(this);
            executing = true;
            command.execute();
        }
    }

    public void stopExecuting() {
        executing = false;
    }


    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onGetCapabilitiesSuccess();

        void onGetCapabilitiesFail(int errno);
    }
}