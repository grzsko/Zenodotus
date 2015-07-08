package gs.zenodotus.back.commands;

import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gs.zenodotus.back.AuthorRow;
import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.Work;
import gs.zenodotus.front.BooksListFragment;

public class GetAuthorsCommand extends AsyncTask<String, Void,
        List<AuthorRow>> {
    private WeakReference<BooksListFragment> hostFragment;

    public GetAuthorsCommand() {
    }

    @Override
    protected List<AuthorRow> doInBackground(String... params) {
        String search_pattern = params[0];
        // TODO check length
//        Log.d("GetAuthorsCommand", "search pattern " + search_pattern);
        DataFactory factory = GlobalDataProvider.getFactory();
//        return factory.getAuthors(search_pattern);
        List<Author> authors = factory.getAuthors(search_pattern);
        List<AuthorRow> packedAuthors = new ArrayList<>();
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            List<Work> books = factory.getWorks(author);
            AuthorRow authorWithBooks = new AuthorRow(author, books);
            packedAuthors.add(authorWithBooks);
        }
        return packedAuthors;
    }

    public GetAuthorsCommand(BooksListFragment booksListFragment) {
        super();
        this.hostFragment = new WeakReference<>(booksListFragment);
    }

    @Override
    protected void onPostExecute(List<AuthorRow> authors) {
        super.onPostExecute(authors);
        hostFragment.get().fillRowsWithAuthors(authors);
    }
}
