package gs.zenodotus.front;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.MissingFormatArgumentException;

import gs.zenodotus.R;
import gs.zenodotus.back.commands.GetEditionsCommand;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Work;

import static gs.zenodotus.back.OnlineDataFactory.getUrnSuffix;

public class MainDisplayActivity extends FragmentActivity
        implements BooksListFragment.OnFragmentInteractionListener,
        EditionsListFragment.EditionsListListener,
        TextDisplayFragment.TextDisplayFragmentListener {

    private static final String TEXT_DISPLAY_STR = "TEXT_DISPLAY";
    private static final String BOOKS_LIST_STR = "BOOKS_LIST";
    private static final String EDITIONS_LIST_STR = "EDITIONS_LIST";

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
                    .add(R.id.fragments_container, firstFragment,
                            BOOKS_LIST_STR).commit();
        }
    }

    @Override
    public void runEditionsFragment(Work work) {
        GetEditionsCommand getEditionsCommand = new GetEditionsCommand(this);
        getEditionsCommand.execute(work);
    }

    public void onGetEditionsSuccess(List<EditionItem> editionItems) {
//        EditionsListFragment editionsListFragment =
//                (EditionsListFragment) getFragmentManager()
//                        .findFragmentById(R.id.editions_list_fragment);
//        if (editionsListFragment != null) {
//            editionsListFragment.setNewAdapter(editionItems);
//        } else {
        EditionsListFragment newFragment = new EditionsListFragment();
        BooksListFragment oldFragment = (BooksListFragment) getFragmentManager()
                .findFragmentByTag(BOOKS_LIST_STR);

        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        transaction.hide(oldFragment);
        transaction
                .add(R.id.fragments_container, newFragment, EDITIONS_LIST_STR);

        transaction.commit();
        newFragment.insertNewEditionsList(editionItems);

        // Hiding keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
//        }
    }

    @Override
    public void onEditionSelected(EditionItem item) {
//        TextDisplayFragment textDisplayFragment =
//                (TextDisplayFragment) getFragmentManager()
//                        .findFragmentById(R.id.text_fragment);
//        if (textDisplayFragment != null) {
//            Log.d("MainDisplayActivity", "text_display_exists");
//            textDisplayFragment.setItemToShow(item);
//
//        } else {
        TextDisplayFragment newFragment = new TextDisplayFragment();

        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        EditionsListFragment oldFragment =
                (EditionsListFragment) getFragmentManager()
                        .findFragmentByTag(EDITIONS_LIST_STR);
        transaction.hide(oldFragment);
        transaction
                .add(R.id.fragments_container, newFragment, TEXT_DISPLAY_STR);
        transaction.commit();
        newFragment.setItemToShow(item);
//        }
    }

    @Override
    public void showDialog(List<String> textChunks, EditionItem item,
                           int position) {
        JumpToTextDialogFragment newFragment = JumpToTextDialogFragment
                .newInstance(textChunks, item, position);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed() {
        TextDisplayFragment textDisplayFragment =
                (TextDisplayFragment) getFragmentManager()
                        .findFragmentByTag(TEXT_DISPLAY_STR);
        EditionsListFragment editionsListFragment =
                (EditionsListFragment) getFragmentManager()
                        .findFragmentByTag(EDITIONS_LIST_STR);
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        if (textDisplayFragment != null) {
            textDisplayFragment.cancelCommands();
            transaction.remove(textDisplayFragment);
            transaction.show(editionsListFragment);
        } else if (editionsListFragment != null) {
            transaction.remove(editionsListFragment);
            BooksListFragment booksListFragment =
                    (BooksListFragment) getFragmentManager()
                            .findFragmentByTag(BOOKS_LIST_STR);
            booksListFragment.cancelCommands();
            transaction.show(booksListFragment);
        }
        transaction.commit();
    }

    private TextDisplayFragment doClick(DialogInterface dialog) {
        dialog.dismiss();
        TextDisplayFragment textDisplayFragment =
                (TextDisplayFragment) getFragmentManager()
                        .findFragmentByTag(TEXT_DISPLAY_STR);
        return textDisplayFragment;
    }

    public void doPositiveClick(DialogInterface dialog, int whichButton) {
        TextDisplayFragment textDisplayFragment = doClick(dialog);
        textDisplayFragment.showTextFromOutside(whichButton);
    }

    public void doNegativeClick(DialogInterface dialog) {
        TextDisplayFragment textDisplayFragment = doClick(dialog);
        textDisplayFragment.updateButtonsVisibility();
    }

    public static class JumpToTextDialogFragment extends DialogFragment {

        private static List<String> urns;
        private static EditionItem item;
        private static int chosenOption;

        public static JumpToTextDialogFragment newInstance(
                List<String> textChunks, EditionItem item, int index) {
            JumpToTextDialogFragment frag = new JumpToTextDialogFragment();
            frag.setCollections(textChunks, item, index);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.jump_title);
            dialog.setNegativeButton(R.string.cancel_button_text,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            ((MainDisplayActivity) getActivity())
                                    .doNegativeClick(dialog);
                        }
                    });
            dialog.setSingleChoiceItems(getReadableLabels(), chosenOption,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            ((MainDisplayActivity) getActivity())
                                    .doPositiveClick(dialog, whichButton);
                        }
                    });
            return dialog.create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            ((MainDisplayActivity) getActivity()).doNegativeClick(dialog);
        }

        private String[] getReadableLabels() {
            if (item.hasMappingInfo) {
                String[] readableLabels = new String[urns.size()];
                String mappingString = item.mappingInfo;
                mappingString = mappingString.replace(":", ", ");
                mappingString = mappingString.replace("=", " ");
                mappingString = mappingString.substring(1);
                for (int i = 0; i < urns.size(); i++) {
                    try {
                        try {
                            readableLabels[i] = String.format(mappingString,
                                    getUrnSuffix(urns.get(i), item));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            readableLabels[i] = urns.get(i);
                        }
                    } catch (MissingFormatArgumentException e) {
                        readableLabels[i] = urns.get(i);
                    }

                }
                return readableLabels;
            } else {
                return urns.toArray(new String[urns.size()]);
            }
        }

        public void setCollections(List<String> textChunks, EditionItem itemArg,
                                   int index) {
            urns = textChunks;
            item = itemArg;
            chosenOption = index;
        }

        @Override
        public void onDestroyView() {
            if (getDialog() != null && getRetainInstance()) {
                getDialog().setDismissMessage(null);
            }
            super.onDestroyView();
        }
    }
}
