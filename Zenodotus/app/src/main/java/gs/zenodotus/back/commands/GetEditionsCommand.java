package gs.zenodotus.back.commands;

import android.os.AsyncTask;

import com.activeandroid.query.Select;

import java.lang.ref.WeakReference;
import java.util.List;

import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Work;
import gs.zenodotus.front.MainDisplayActivity;

public class GetEditionsCommand
        extends AsyncTask<Work, Void, List<EditionItem>> {

    private WeakReference<MainDisplayActivity> hostActivity;

    public GetEditionsCommand(MainDisplayActivity mainDisplayActivity){
        super();
        this.hostActivity = new WeakReference<>(mainDisplayActivity);
    }

    @Override
    protected List<EditionItem> doInBackground(Work... params) {
        Work work = params[0];
        return new Select().from(EditionItem.class).where("work = ?", work.getId())
                .execute();
    }

    @Override
    protected void onPostExecute(List<EditionItem> editionItems) {
        super.onPostExecute(editionItems);
        hostActivity.get().onGetEditionsSuccess(editionItems);
    }
}
