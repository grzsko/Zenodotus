package gs.zenodotus.back.commands;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.xml.XmlNode;
import gs.zenodotus.front.TextDisplayFragment;

public class GetValidReffCommand extends AsyncTask<String, Void, List<String>> {
    public static final int CORRECT_EXECUTION = 0;
    public static final int PERSEUS_INTERNET_PROBLEM = 1;
    public static final int INCORRECT_PERSEUS_ANSWER = 2;
    private static final int FIRST_ATTEMPT = 1;
    private static final int SECOND_ATTEMPT = 2;
    private WeakReference<TextDisplayFragment> hostFragment;
    private int result = CORRECT_EXECUTION;
    private int attempt = FIRST_ATTEMPT;
            // sometimes there is no urn for edition item,
    // but instead of it there is a correct urn for whole work

    public GetValidReffCommand(TextDisplayFragment textDisplayFragment) {
        this.hostFragment = new WeakReference<>(textDisplayFragment);
    }

    private List<String> queryPerseus(String... params)
            throws PerseusProblemException {
        DataFactory dataFactory = GlobalDataProvider.getFactory();
        XmlNode urnsWrapped;
        String urn = (attempt == 1) ? params[0] : params[1];
        try {
            urnsWrapped = dataFactory.getValidReffFromPerseus(urn);
        } catch (IOException e) {
            e.printStackTrace();
            result = PERSEUS_INTERNET_PROBLEM;
            return null;
        } catch (XmlPullParserException e) {
            result = INCORRECT_PERSEUS_ANSWER;
            return null;
        }
        if (!isCancelled() && result == 0) {
            XmlNode urns;
            try {
                urns = urnsWrapped.getChild("reply").getChild("reff");
            } catch (XmlNode.XmlNodeException e) {
                if (attempt == FIRST_ATTEMPT) {
                    attempt++;
                    return queryPerseus(params);
                } else {
                    result = INCORRECT_PERSEUS_ANSWER;
                    return null;
                }
            }
            if (urns == null) {
                throw new PerseusProblemException("no correct tree");
            }
            List<String> textChunks = new ArrayList<>();
            int numberOfChunks = urns.getChildrenSize();
            for (int i = 0; i < numberOfChunks; i++) {
                try {
                    textChunks.add(urns.getChild(i).getText());
                } catch (XmlNode.XmlNodeException e) {
                    // very serious problem (this catch should not be reached),
                    // no more attempts
                    result = INCORRECT_PERSEUS_ANSWER;
                    return null;
                }
            }
            return textChunks;
        }
        return null;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        try {
            return queryPerseus(params);
        } catch (PerseusProblemException e) {
            e.printStackTrace();
            result = INCORRECT_PERSEUS_ANSWER;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> textChunks) {
        if (result == CORRECT_EXECUTION) {
            hostFragment.get().onGetValidReffsSuccess(textChunks);
        } else {
            hostFragment.get().onGetValidReffFail(result);
        }
    }
}
