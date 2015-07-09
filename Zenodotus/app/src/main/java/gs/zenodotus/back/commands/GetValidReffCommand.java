package gs.zenodotus.back.commands;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.XmlNode;
import gs.zenodotus.front.TextDisplayFragment;

public class GetValidReffCommand extends AsyncTask<String, Void, List<String>> {
    private WeakReference<TextDisplayFragment> hostFragment;

    public GetValidReffCommand(TextDisplayFragment textDisplayFragment) {
        this.hostFragment = new WeakReference<>(textDisplayFragment);
    }

    @Override
    protected List<String> doInBackground(String... params) {
        DataFactory dataFactory = GlobalDataProvider.getFactory();
        XmlNode urnsWrapped = null;
        try {
            urnsWrapped = dataFactory.getValidReffFromPerseus(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO some handling!
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        // TODO errors handling!
        XmlNode urns = urnsWrapped.getChild("reply").getChild("reff");
        Log.d("GetValidReffCommmmm", "" + (urns != null));
        List<String> textChunks = new ArrayList<>();
        int numberOfChunks = urns.getChildrenSize();
        for (int i = 0; i < numberOfChunks; i++) {
            textChunks.add(urns.getChild(i).getText());
        }
        return textChunks;
    }

    @Override
    protected void onPostExecute(List<String> textChunks) {
        hostFragment.get().onGetValidReffsSuccess(textChunks);
    }
}
