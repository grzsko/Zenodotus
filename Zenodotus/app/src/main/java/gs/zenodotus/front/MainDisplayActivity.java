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
        EditionsListFragment.EditionsListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_display);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragments_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            BooksListFragment firstFragment = new BooksListFragment();

            // In case this activity was started with special instructions
            // from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragments_container, firstFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {

    }

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
//        EditionsListFragment editionsListFragment = null;
        if (editionsListFragment != null) {
            Log.d("MainDisplayActivity", "null");
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            editionsListFragment.insertNewEditionsList(editionItems);

        } else {
            Log.d("MainDisplayActivity", "NOT null");
            // If the frag is not available, we're in the one-pane layout and
            // must swap frags...

            // Create fragment and give it an argument for the selected article
            EditionsListFragment newFragment = new EditionsListFragment();
            newFragment.insertNewEditionsList(editionItems);
//            Bundle args = new Bundle();
//            args.putInt(A.ARG_POSITION, position);
//            newFragment.setArguments(args);
            FragmentTransaction transaction =
                    getFragmentManager().beginTransaction();

            // Replace whatever is in the fragments_container view with this
            // fragment,
            // and add the transaction to the back stack so the user can
            // navigate back
            transaction.replace(R.id.fragments_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        }
    }

    @Override
    public void onEditionSelected(int position) {
        // TODO write here smth in the future
    }
}
