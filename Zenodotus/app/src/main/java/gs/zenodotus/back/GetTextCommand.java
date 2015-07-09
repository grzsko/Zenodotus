package gs.zenodotus.back;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;

import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.front.TextDisplayFragment;

public class GetTextCommand extends AsyncTask<String, Void, String> {
    WeakReference<TextDisplayFragment> hostFragment;
    private EditionItem editionItem;

    public GetTextCommand(EditionItem item, TextDisplayFragment hostFragment) {
        this.editionItem = item;
        this.hostFragment = new WeakReference<>(hostFragment);
    }

    @Override
    protected String doInBackground(String... params) {
        DataFactory factory = GlobalDataProvider.getFactory();
        try {
            return factory.getTextChunk(params[0], editionItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        hostFragment.get().onGetTextSuccess(s);
    }
}
