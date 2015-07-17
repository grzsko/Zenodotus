package gs.zenodotus.back.commands;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.front.TextDisplayFragment;

public class GetTextCommand extends AsyncTask<String, Void, String> {
    public static final int LOST_CONNECTION = 1;
    public static final int PERSEUS_PROBLEM = 2;
    WeakReference<TextDisplayFragment> hostFragment;
    int result = 0;
    private EditionItem editionItem;

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
        if (result == 0) {
            hostFragment.get().onGetTextSuccess(s);
        } else {
            hostFragment.get().onGetTextFail(result);
        }
    }
}
