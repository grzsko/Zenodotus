package gs.zenodotus.back.commands;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.xml.XmlNode;
import gs.zenodotus.front.TaskFragment;

public class GetCapabilitiesCommand extends AsyncTask<Void, Void, Void> {
    public static final int LOST_CONNECTION = 1;
    public static final int BAD_ANSWER = 2;
    private TaskFragment hostFragment;
    private int result = 0;

    public GetCapabilitiesCommand(TaskFragment taskFragment) {
        this.hostFragment = taskFragment;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        if (GlobalDataProvider.areCapabillitiesActual(hostFragment.getActivity())) {
            return null;
        } else {
            DataFactory dataFactory = GlobalDataProvider.getFactory();
            try {
                dataFactory.storeCapabilitiesInDb();
            } catch (IOException e) {
                e.printStackTrace();
                this.result = LOST_CONNECTION;
            } catch (XmlNode.XmlNodeException | XmlPullParserException e) {
                e.printStackTrace();
                this.result = BAD_ANSWER;
            }
            GlobalDataProvider.setCapabilitiesActual(hostFragment.getActivity());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void arg) {
        super.onPostExecute(arg);
        if (result == 0) {
            this.hostFragment.getTaskCallbacks().onGetCapabilitiesSuccess();
        } else {
            this.hostFragment.getTaskCallbacks().onGetCapabilitiesFail(result);
        }
    }
}
