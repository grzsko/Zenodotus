package gs.zenodotus.back;

import android.util.Log;

import com.activeandroid.ActiveAndroid;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Language;
import gs.zenodotus.back.database.Work;

/**
 * Factory for online communication with Perseus server or working on "real"
 * data cached in db.
 *
 * @author gskoraczynski
 */
public class OnlineDataFactory extends DataFactory {

    @Override
    protected InputStream getXmlFromPerseus(String urlString)
            throws IOException {
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();
        InputStream in =
                new BufferedInputStream(urlConnection.getInputStream());
        return in;
    }

    @Override
    protected XmlNode getCapabilitiesFromPerseus() {
        // TODO check if internet connection is available
        // https://developer.android
        // .com/training/basics/network-ops/connecting.html#connection
        String url = "http://www.perseus.tufts" +
                ".edu/hopper/CTS?request=GetCapabilities";
        InputStream xmlStream;
        try {
            xmlStream = this.getXmlFromPerseus(url);
            XmlParser parser = new XmlParser();
            XmlNode returnTree = parser.parse(xmlStream);
            return returnTree;
        } catch (IOException e) {
            e.printStackTrace();
            // TODO move handling these exceptions somewhere
            return null;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void storeCapabilitiesInDb() {
        // TODO refactor this!
        XmlNode parsedCapabilities = this.getCapabilitiesFromPerseus();
        Log.d("Storing capabilities1", parsedCapabilities.getName());
        int numberOfTextgroups = parsedCapabilities.getChildrenSize();
        List<Author> authors = new LinkedList<>();
        List<Work> works = new LinkedList<>();
        List<EditionItem> editions = new LinkedList<>();
        for (int i = 0; i < numberOfTextgroups; i++) {
//            Log.d("Storing capabilities", "In loop");
            XmlNode authorNode = parsedCapabilities.getChild(i);
            Log.d("Storing capabilities2", authorNode.getName());
            if (authorNode.getName().equals("textgroup")) {
                Author author =
                        new Author(authorNode.getChild("groupname").getText());
//                author.save();
                int numberOfHerWorks = authorNode.getChildrenSize();
                for (int j = 0; j < numberOfHerWorks; j++) {
                    XmlNode workNode = authorNode.getChild(j);
                    if (workNode.getText().equals("work")) {
                        Work work =
                                new Work(workNode.getChild("title").getText(),
                                        author, workNode.getAttribute("urn"),
                                        Language.fromAbbreviation(
                                                workNode.getAttribute(
                                                        "xml:lang")));
                        works.add(work);
                        int numberOfItsEditions = workNode.getChildrenSize();
                        for (int k = 0; k < numberOfItsEditions; k++) {
                            XmlNode editionNode = workNode.getChild(k);
                            Language language = Language.fromAbbreviation(
                                    editionNode.getAttribute("xml:lang"));
                            if (language == null) {
                                language = work.getLanguage();
                            }
                            EditionItem edition = new EditionItem(
                                    editionNode.getChild("label").getText(),
                                    language, work);
                            editions.add(edition);
                        }
                    }
                }
                authors.add(author);
            } else {
                continue;
            }
        }
        ActiveAndroid.beginTransaction();
        try {
            for (Author author : authors) {
                author.save();
            }
            for (Work work : works) {
                work.save();
            }
            ActiveAndroid.setTransactionSuccessful();
            for (EditionItem edition : editions) {
                edition.save();
            }
        } finally {
            ActiveAndroid.endTransaction();
        }
//        ActiveAndroid.beginTransaction();
//        try {
//            for (Work work : works) {
//                work.save();
//            }
//
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
//        ActiveAndroid.beginTransaction();
//        try {
//            for (EditionItem edition : editions) {
//                edition.save();
//            }
//
//            ActiveAndroid.setTransactionSuccessful();
//        } finally {
//            ActiveAndroid.endTransaction();
//        }
        Log.v("Storing capabilities3", authors.size() + "");
    }

}
