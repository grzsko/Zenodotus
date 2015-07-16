package gs.zenodotus.front;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.AuthorRow;
import gs.zenodotus.back.ExpandableListAdapter;
import gs.zenodotus.back.commands.GetAuthorsCommand;
import gs.zenodotus.back.database.Work;

public class BooksListFragment extends Fragment {
    ExpandableListAdapter adapter;
    GetAuthorsCommand command;
    Parcelable state;
    private final TextWatcher authorTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        public void afterTextChanged(Editable pattern) {
            if (command != null) {
                command.cancel(true);
            }
            command = new GetAuthorsCommand(BooksListFragment.this);
            command.execute(pattern.toString());
        }
    };
    ExpandableListView listView;
    private OnFragmentInteractionListener mListener;

    public BooksListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BooksListF", "onCreate");
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =
                inflater.inflate(R.layout.fragment_books_list, container,
                        false);
        EditText editText =
                (EditText) rootView.findViewById(R.id.author_search_text);
        editText.addTextChangedListener(authorTextWatcher);
        listView =
                (ExpandableListView) rootView.findViewById(R.id.listView);
//            listView.setAdapter(adapter);
//        listView.setSaveEnabled(true);
////        if (adapter != null) {
//            if (state != null) {
//                listView.onRestoreInstanceState(state);
//            }
////        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void fillRowsWithAuthors(List<AuthorRow> authors) {
        ExpandableListView listView =
                (ExpandableListView) ((MainDisplayActivity) mListener)
                        .findViewById(R.id.listView);
        ExpandableListAdapter adapter =
                new ExpandableListAdapter(this, authors);
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }

    public void openEditions(Work work) {
        mListener.runEditionsFragment(work);
    }

    public void cancelCommands() {
        if (command != null) {
            command.cancel(true);
        }
    }

    public interface OnFragmentInteractionListener {
        void runEditionsFragment(Work work);
    }
}
