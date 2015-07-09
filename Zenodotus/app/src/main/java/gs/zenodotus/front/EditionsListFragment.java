package gs.zenodotus.front;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.EditionsListAdapter;
import gs.zenodotus.back.database.EditionItem;

public class EditionsListFragment extends ListFragment {
    EditionsListListener mCallback;
    int layout;
    private List<EditionItem> items;

    public EditionsListFragment() {
        super();
    }

    public void setNewAdapter(List<EditionItem> editionItems) {
        insertNewEditionsList(editionItems);
        setListAdapter(new EditionsListAdapter(getActivity(), layout, items));
    }

    public void insertNewEditionsList(List<EditionItem> editionItems) {
        this.items = editionItems;
        Log.d("EditionsListFragment", "inserting new list");
        // TODO should u do here something more?
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.layout = R.layout.editions_row;


        // Create an array adapter for the list view, using the Ipsum
        // headlines array

//        setListAdapter(new EditionsListAdapter(getActivity(), layout, items));

        Log.d("MaFragmentOnCreate", "After creating adapter");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("EditionsListFragment", "onCreateView");
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
//        if (savedInstanceState != null) {
//            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
//        }
//
//        // Inflate the layout for this fragment
        // TODO uncomment this fragment!
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListShown(true);
        if (items != null) {
            setListAdapter(
                    new EditionsListAdapter(getActivity(), layout, items));
            // TODO check on normal tablet if it works!
        }
        // When in two-pane layout, set the listview to highlight the
        // selected list item
        // (We do this during onStart because at the point the listview is
        // available.)
//        if (getFragmentManager()
//                .findFragmentById(R.id.editions_list_fragment) != null) {
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (EditionsListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        EditionItem dataFromPosition =
                (EditionItem) getListView().getItemAtPosition(position);

        Log.d("EditionsListFragment", "Will crash?");
        Log.d("EditionsListFragment", dataFromPosition.label);

        // Notify the parent activity of selected item
        mCallback.onEditionSelected(dataFromPosition);

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }

    public interface EditionsListListener {
        public void onEditionSelected(EditionItem item);
    }
}
