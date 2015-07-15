package gs.zenodotus.front;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.commands.GetEditionsCommand;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Work;

public class MainDisplayActivity extends FragmentActivity
        implements BooksListFragment.OnFragmentInteractionListener,
        EditionsListFragment.EditionsListListener,
        TextDisplayFragment.TextDisplayFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);

        if (findViewById(R.id.fragments_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
            BooksListFragment firstFragment = new BooksListFragment();
            firstFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragments_container, firstFragment).commit();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void runEditionsFragment(Work work) {
        GetEditionsCommand getEditionsCommand = new GetEditionsCommand(this);
        getEditionsCommand.execute(work);
    }

    public void onGetEditionsSuccess(List<EditionItem> editionItems) {
        EditionsListFragment editionsListFragment =
                (EditionsListFragment) getFragmentManager()
                        .findFragmentById(R.id.editions_list_fragment);
        Log.d("MainDisplayActivity", "getEditionsSuccess");
        if (editionsListFragment != null) {
            Log.d("MainDisplayActivity", "NOT null");
//            editionsListFragment.insertNewEditionsList(editionItems);
            editionsListFragment.setNewAdapter(editionItems);
        } else {
            Log.d("MainDisplayActivity", "null");

            EditionsListFragment newFragment = new EditionsListFragment();

            FragmentTransaction transaction =
                    getFragmentManager().beginTransaction();

            transaction.replace(R.id.fragments_container, newFragment,
                    "EDITIONS_LIST");
            transaction.addToBackStack(null);

            transaction.commit();
            newFragment.insertNewEditionsList(editionItems);

        }
    }

    @Override
    public void onEditionSelected(EditionItem item) {
        TextDisplayFragment textDisplayFragment =
                (TextDisplayFragment) getFragmentManager()
                        .findFragmentById(R.id.text_fragment);
        if (textDisplayFragment != null) {
            Log.d("MainDisplayActivity", "text_display_exists");
            textDisplayFragment.setItemToShow(item);

        } else {
            TextDisplayFragment newFragment = new TextDisplayFragment();

            FragmentTransaction transaction =
                    getFragmentManager().beginTransaction();
            EditionsListFragment oldFragment = (EditionsListFragment)
                    getFragmentManager().findFragmentByTag("EDITIONS_LIST");
            transaction.hide(oldFragment);
//            transaction.replace(R.id.fragments_container, newFragment);
            transaction.add(R.id.fragments_container, newFragment,
                    "TEXT_DISPLAY");
//            transaction.addToBackStack(null);
            // TODO correct pressing back button
            transaction.commit();
            Log.d("MaindisplayActivity", "before new set item to show");
            newFragment.setItemToShow(item);
        }
    }

    @Override
    public void onTextDisplayFragmentInteraction() {
    }

    @Override
    public void onBackPressed() {
        // TODO END THIS FUNCTION!
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        TextDisplayFragment oldFragment = (TextDisplayFragment)
                getFragmentManager().findFragmentByTag("TEXT_DISPLAY");
        transaction.remove(oldFragment);
        EditionsListFragment newFragment = (EditionsListFragment) getFragmentManager()
                .findFragmentByTag("EDITIONS_LIST");
        transaction.show(newFragment);
        transaction.commit();
    }
}
