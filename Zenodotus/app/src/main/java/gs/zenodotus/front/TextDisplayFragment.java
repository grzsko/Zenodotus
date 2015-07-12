package gs.zenodotus.front;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

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

    public TextDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_display, container,
                false);
        if (actualText >= 0 && texts[actualText] != null) {
            WebView webView = (WebView) view.findViewById(R.id.webview);
            putTextIntoGivenView(webView);
            setContentVisible(view);
        }
        return view;
    }

    private void setContentVisible() {
        // TODO write here smth!
//        setContentVisible(view);
    }

    private void setContentVisible(View view) {
        // TODO write here smth!
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
            putTextIntoView();
            setContentVisible();
        }
    }

    private void putTextIntoGivenView(WebView webView) {
        String html = getFullHtml(texts[actualText]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        Log.d("putting text into", html);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html",
                "UTF-8", null);
//        webView.loadData(html, "text/html; charset=utf-8\"", null);
    }

    private void putTextIntoView() {
        WebView webView = (WebView) getView().findViewById(R.id.webview);
        putTextIntoGivenView(webView);
    }

    private String getFullHtml(String body) {
        String html = "<html><head><link rel=\"stylesheet\" " +
                "type=\"text/css\" href=\"style.css\" /></head><body>";
        html += body.replace("div1", "div");
        return html + "</body></html>";
    }

    public void onGetTextSuccess(String textChunk) {
        texts[actualText] = textChunk;
        putTextIntoView();
        setContentVisible();
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

    public interface TextDisplayFragmentListener {
        public void onTextDisplayFragmentInteraction();
    }

}
