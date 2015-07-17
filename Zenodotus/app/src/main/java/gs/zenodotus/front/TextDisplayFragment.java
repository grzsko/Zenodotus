package gs.zenodotus.front;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.List;

import gs.zenodotus.R;
import gs.zenodotus.back.commands.GetTextCommand;
import gs.zenodotus.back.commands.GetValidReffCommand;
import gs.zenodotus.back.database.EditionItem;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link gs.zenodotus.front.TextDisplayFragment.TextDisplayFragmentListener}
 * interface
 * to handle interaction events.
 */
public class TextDisplayFragment extends Fragment {

    int actualText = -1;
    private EditionItem item;
    private TextDisplayFragmentListener mListener;
    private List<String> textChunksUrns;
    private String[] texts;
    private boolean textDisplayed = false;
    private boolean progressbarHided = false;

    private WebView webView;
    private CircularProgressView circularProgressView;
    private MenuItem buttonLeft;
    private MenuItem buttonRight;
    private MenuItem buttonJump;
    private GetValidReffCommand getValidReffCommand;
    private GetTextCommand getTextCommand;

    public TextDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_display, container,
                false);
        webView = (WebView) view.findViewById(R.id.text_display_webview);
        circularProgressView = (CircularProgressView) view
                .findViewById(R.id.text_display_progress_view);
        if (actualText >= 0 && texts[actualText] != null) {
            putTextIntoGivenView();
//            setContentVisible();
        }
        if (progressbarHided) {
            hideProgressBar();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_display, menu);
        buttonLeft = menu.findItem(R.id.action_left);
        buttonRight = menu.findItem(R.id.action_right);
        buttonJump = menu.findItem(R.id.action_jump);
        updateButtonsVisibility();
    }

    private void setContentVisible(boolean visible) {
//        textDisplayed = true;
        progressbarHided = !visible;
        circularProgressView.setVisibility(visible ? View.GONE : View.VISIBLE);
//        WebView webView = (WebView) getView().findViewById(R.id
//                .text_display_webview);
        webView.setVisibility(visible ? View.VISIBLE : View.GONE);
        textDisplayed = visible;
        updateButtonsVisibility();
    }

    void updateButtonsVisibility() {
        buttonLeft.setEnabled(textDisplayed && actualText > 0);
        buttonRight.setEnabled(
                textDisplayed && actualText < textChunksUrns.size() - 1);
        buttonJump.setEnabled(textDisplayed);

    }

    void hideProgressBar() {
        circularProgressView.setVisibility(View.GONE);
        progressbarHided = true;
    }

    private void disableButtons() {
        buttonLeft.setEnabled(false);
        buttonRight.setEnabled(false);
        buttonJump.setEnabled(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TextDisplayFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_left:
                goToPreviousPage();
                return true;
            case R.id.action_right:
                goToNextPage();
                return true;
            case R.id.action_jump:
                chosePage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void chosePage() {
        disableButtons();
        mListener.showDialog(textChunksUrns, item, actualText);
    }

    private void jumpOnePage(int change) {
        disableButtons();
        setContentVisible(false);
        showText(actualText + change);
    }

    private void goToNextPage() {
        jumpOnePage(1);
    }

    private void goToPreviousPage() {
        jumpOnePage(-1);
    }

    public void fetchValidRefsIfItemExists() {
        if (item != null) {
            fetchValidRefsForItem(this.item);
        }
    }

    private void fetchValidRefsForItem(EditionItem item) {
        getValidReffCommand = new GetValidReffCommand(this);
        getValidReffCommand.execute(item.urn, item.work.urn);
    }

    public void setItemToShow(EditionItem item) {
        this.item = item;
        this.texts = null;
        this.actualText = -1;
        this.textChunksUrns = null;
        fetchValidRefsIfItemExists();
    }

    public void onGetValidReffsSuccess(List<String> textChunks) {
        this.textChunksUrns = textChunks;
        this.texts = new String[textChunks.size()];
        showText(0);
    }

    private void showText(int position) {
        actualText = position;
        if (texts[position] == null) {
            getTextCommand = new GetTextCommand(item, this);
            getTextCommand.execute(textChunksUrns.get(position));
        } else {
            putTextIntoGivenView();
        }
    }

    private void putThisTextIntoView(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html",
                "UTF-8", null);
        setContentVisible(true);
        updateButtonsVisibility();
    }

    private void putTextIntoGivenView() {
        String html = getFullHtml(texts[actualText]);
        putThisTextIntoView(html);
    }

    private String getFullHtml(String body) {
        String html = "<html><head><link rel=\"stylesheet\" " +
                "type=\"text/css\" href=\"style.css\" /></head><body>";
        html += body.replace("div1", "div").replace("tei:div", "div");
        return html + "</body></html>";
    }

    private String getEmptyHtml() {
        return getFullHtml("");
    }

    public void onGetTextSuccess(String textChunk) {
        texts[actualText] = textChunk;
        putTextIntoGivenView();
    }

    public void onGetTextFail(int errno) {
        String message = getString(R.string.get_text_crashed_text);
        showErrorAlert(message);
        texts[actualText] = "";
        putTextIntoGivenView();
    }

    public void onGetValidReffFail(int result) {
        String message = getString(R.string.get_validreff_crashed_text);
        showErrorAlert(message);
        hideProgressBar();
        disableButtons();
    }

    private void showErrorAlert(String message) {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder((MainDisplayActivity) mListener);
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setNegativeButton(getString(R.string.got_it_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showTextFromOutside(int whichButton) {
        setContentVisible(false);
        disableButtons();
        showText(whichButton);
    }

    public void cancelCommands() {
        if (getValidReffCommand != null) {
            getValidReffCommand.cancel(true);
        }
        if (getTextCommand != null) {
            getTextCommand.cancel(true);
        }
    }

    public interface TextDisplayFragmentListener {
        void showDialog(List<String> textChunks, EditionItem item,
                        int position);
    }
}
