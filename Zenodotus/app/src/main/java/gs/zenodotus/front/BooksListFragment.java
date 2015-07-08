package gs.zenodotus.front;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.AuthorRow;
import gs.zenodotus.back.ExpandableListAdapter;
import gs.zenodotus.back.commands.GetAuthorsCommand;
import gs.zenodotus.back.database.Work;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BooksListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * // * Use the {@link BooksListFragment#newInstance} factory method to
 * // * create an instance of this fragment.
 */
public class BooksListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @return A new instance of fragment BooksListFragment.
//     */
//    public static BooksListFragment newInstance() {
//        BooksListFragment fragment = new BooksListFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    public BooksListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =
                inflater.inflate(R.layout.fragment_books_list, container,
                        false);
//        ExpandableListView expandableListView =
//                (ExpandableListView) rootView.findViewById(R.id.listView);
//        expandableListView.setAdapter(
//                new ExpandableListAdapter(inflater, groupList,
// dataCollection));
        Button button = (Button) rootView.findViewById(R.id.button_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) rootView
                        .findViewById(R.id.author_search_text);
                String searched_text = editText.getText().toString();
                GetAuthorsCommand command =
                        new GetAuthorsCommand(BooksListFragment.this);
                command.execute(searched_text);
            }
        });
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
        Log.d("Fragment", "Authors found " + authors.size());
        for (int i = 0; i < authors.size(); i++) {
            Log.d("Fragment", "Their names " + authors.get(i).author.name);
        }
        ExpandableListView listView =
                (ExpandableListView) getActivity().findViewById(R.id.listView);
        ExpandableListAdapter adapter =
                new ExpandableListAdapter(this, authors);
        listView.setAdapter(adapter);
        // TODO do u will always get an activity instance?
    }

    public void openEditions(Work work) {
        mListener.runEditionsFragment(work);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating
     * .html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
        public void runEditionsFragment(Work work);
    }

}
