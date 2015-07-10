package gs.zenodotus.back.commands;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gs.zenodotus.back.AuthorRow;
import gs.zenodotus.back.DataFactory;
import gs.zenodotus.back.GlobalDataProvider;
import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.Work;
import gs.zenodotus.front.BooksListFragment;

public class GetAuthorsCommand
        extends AsyncTask<String, Void, List<AuthorRow>> {
    private WeakReference<BooksListFragment> hostFragment;

    public GetAuthorsCommand() {
    }

    public GetAuthorsCommand(BooksListFragment booksListFragment) {
        super();
        this.hostFragment = new WeakReference<>(booksListFragment);
    }

    @Override
    protected List<AuthorRow> doInBackground(String... params) {
        String search_pattern = params[0];
        if (search_pattern != null) {
            DataFactory factory = GlobalDataProvider.getFactory();
            if (!isCancelled()) {
                List<Author> authors = factory.getAuthors(search_pattern);
                List<AuthorRow> packedAuthors = new ArrayList<>();
                for (int i = 0; i < authors.size(); i++) {
                    if (isCancelled()) {
                        return null;
                    } else {
                        Author author = authors.get(i);
                        List<Work> books = factory.getWorks(author);
                        AuthorRow authorWithBooks =
                                new AuthorRow(author, books);
                        packedAuthors.add(authorWithBooks);
                    }
                }
                if (isCancelled()) {
                    return null;
                } else {
                    return packedAuthors;
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<AuthorRow> authors) {
        super.onPostExecute(authors);
        hostFragment.get().fillRowsWithAuthors(authors);
    }
}
