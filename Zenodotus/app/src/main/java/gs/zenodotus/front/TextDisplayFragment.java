package gs.zenodotus.front;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

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

    private WebView webView;
    private CircularProgressView circularProgressView;
    private MenuItem buttonLeft;
    private MenuItem buttonRight;
    private MenuItem buttonJump;

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
        Log.d("onoptionsitemselected", "clicked!");
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
        mListener.showDialog(textChunksUrns, item);
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
        GetValidReffCommand command = new GetValidReffCommand(this);
        command.execute(item.urn, item.work.urn);
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
            GetTextCommand command = new GetTextCommand(item, this);
            command.execute(textChunksUrns.get(position));
        } else {
            putTextIntoGivenView();
        }
    }

    private void putTextIntoGivenView() {
        String html = getFullHtml(texts[actualText]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        Log.d("putting text into", html);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html",
                "UTF-8", null);
        setContentVisible(true);
        updateButtonsVisibility();
    }

    private String getFullHtml(String body) {
        String html = "<html><head><link rel=\"stylesheet\" " +
                "type=\"text/css\" href=\"style.css\" /></head><body>";
        html += body.replace("div1", "div");
        return html + "</body></html>";
    }

    public void onGetTextSuccess(String textChunk) {
        texts[actualText] = textChunk;
        putTextIntoGivenView();
    }

    public void onGetTextFail(int errno) {
        // TODO write here smth
    }

    public void onGetValidReffFail(int result) {
        int duration = Toast.LENGTH_LONG;
        // TODO do it better!
        Toast toast = Toast.makeText((MainDisplayActivity) mListener,
                "GetValidReff crashed", duration);
        toast.show();
    }

    public void showTextFromOutside(int whichButton) {
        setContentVisible(false);
        disableButtons();
        showText(whichButton);
    }

    public interface TextDisplayFragmentListener {
        public void showDialog(List<String> textChunks, EditionItem item);
    }
}
