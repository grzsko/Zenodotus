package gs.zenodotus.back.commands;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.xml.transform.Result;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.front.TextDisplayFragment;

public class GetTextCommand extends AsyncTask<String, Void, String> {
    WeakReference<TextDisplayFragment> hostFragment;
    private EditionItem editionItem;
    int result = 0;
    public static final int LOST_CONNECTION = 1;
    public static final int PERSEUS_PROBLEM = 2;

    public GetTextCommand(EditionItem item, TextDisplayFragment hostFragment) {
        this.editionItem = item;
        this.hostFragment = new WeakReference<>(hostFragment);
    }

    @Override
    protected String doInBackground(String... params) {
        DataFactory factory = GlobalDataProvider.getFactory();
        try {
            try {
                return factory.getTextChunk(params[0], editionItem);
            } catch (PerseusProblemException e) {
                result = PERSEUS_PROBLEM;
            }
        } catch (IOException e) {
            result = LOST_CONNECTION;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("GetTextCommand", s);
        if (result == 0) {
            hostFragment.get().onGetTextSuccess(s);
        } else {
            hostFragment.get().onGetTextFail(result);
        }
    }
}
