package gs.zenodotus.front;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

    private EditionItem item;

    private TextDisplayFragmentListener mListener;
    private List<String> textChunksUrns;
    private String[] texts;
    int actualText = -1;

    public TextDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater
                .inflate(R.layout.fragment_text_display, container, false);
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
        fetchValidRefsIfItemExists();
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
        command.execute(item.urn);
    }

    public void setItemToShow(EditionItem item) {
        this.item = item;
        fetchValidRefsIfItemExists();
        // TODO should be here fetchValidRefsIfItemExists?
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
            putTextIntoView(position);
        }
    }

    private void putTextIntoView(int position) {
        String html = getFullHtml(texts[position]);
        WebView webView = (WebView) getView().findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        webView.loadData(html, "text/html; charset=utf-8\"", null);
    }

    private String getFullHtml(String body) {
        String html = "<html><head></head>";
        html += body;
        return html;
    }

    public void onGetTextSuccess(String textChunk) {
        texts[actualText] = textChunk;
        putTextIntoView(actualText);
    }

    public interface TextDisplayFragmentListener {
        // TODO: write here smth if needed
        public void onTextDisplayFragmentInteraction();
    }

}
