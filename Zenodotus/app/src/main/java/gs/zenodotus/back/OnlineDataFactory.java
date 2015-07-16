package gs.zenodotus.back;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Language;
import gs.zenodotus.back.database.Work;
import gs.zenodotus.back.xml.XmlNode;
import gs.zenodotus.back.xml.XmlParser;

/**
 * Factory for online communication with Perseus server or working on "real"
 * data cached in db.
 *
 * @author gskoraczynski
 */
public class OnlineDataFactory extends DataFactory {

    private String CTS_GECAPABILITIES_ADDR = "http://www.perseus.tufts" +
            ".edu/hopper/CTS?request=GetCapabilities";
    private String CTS_GETVALIDREFF_ADDR_PREF = "http://www.perseus.tufts" +
            ".edu/hopper/CTS?request=GetValidReff&urn=";
    private String CST_GETPASSAGE_ADDR_PREF =
            "http://www.perseus.tufts.edu/hopper/CTS?request=GetPassage&urn=";
    private String OLD_PERSEUS_GETPASSAGE_ADDR_PREF = "http://www.perseus" +
            ".tufts.edu/hopper/xmlchunk?doc=Perseus:text:";

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
    protected XmlNode getCapabilitiesFromPerseus()
            throws IOException, XmlPullParserException {
        String url = CTS_GECAPABILITIES_ADDR;
        InputStream xmlStream;
        xmlStream = this.getXmlFromPerseus(url);
        XmlParser parser = new XmlParser();
        return parser.parse(xmlStream);

    }

    private void loadDataToDb(List<Author> authors, List<Work> works,
                              List<EditionItem> editions) {
        ActiveAndroid.beginTransaction();
        try {
            for (Author author : authors) {
                author.save();
            }
            for (Work work : works) {
                work.save();
            }
            for (EditionItem edition : editions) {
                edition.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void parseHerEditions(XmlNode workNode, Work work,
                                  List<EditionItem> editions)
            throws XmlNode.XmlNodeException {
        int numberOfItsEditions = workNode.getChildrenSize();
        for (int k = 0; k < numberOfItsEditions; k++) {
            XmlNode editionNode = workNode.getChild(k);
            if (editionNode.getName()
                    .equals(ConstantStringsContainer.EDITION_STR) ||
                    editionNode.getName()
                            .equals(ConstantStringsContainer.TRANSLATION_STR)) {
                Language language;
                if (editionNode
                        .getAttribute(ConstantStringsContainer.LANG_STR) ==
                        null) {
                    language = work.getLanguage();
                } else {
                    language = Language.fromAbbreviation(editionNode
                            .getAttribute(ConstantStringsContainer.LANG_STR));
                }
                boolean hasMappingInfo = false;
                String mappingString = "";
                String oldPerseusId;
                XmlNode onlineNode = editionNode.getChild("online");
                if (onlineNode != null) {
                    oldPerseusId = onlineNode.getAttribute("docname");
                    if (oldPerseusId.endsWith(
                            ConstantStringsContainer.XML_EXTENSION_STR)) {
                        oldPerseusId = oldPerseusId.substring(0,
                                oldPerseusId.length() -
                                        (ConstantStringsContainer
                                                .XML_EXTENSION_STR)
                                                .length());
                    }
                    XmlNode citationMappingNode = onlineNode
                            .getChild(ConstantStringsContainer.CIT_MAPPING_STR);
                    if (citationMappingNode != null) {
                        hasMappingInfo = true;
                        XmlNode citationNode = citationMappingNode.getChild(
                                ConstantStringsContainer.CITATION_STR);
                        while (citationNode != null) {
                            mappingString +=
                                    ":" + citationNode.getAttribute("label") +
                                            "=%s";
                            citationNode = citationNode.getChild(
                                    ConstantStringsContainer.CITATION_STR);
                        }
                        Log.d("parseHerEditions", "has NO citationMapping!");
                    }
                } else {
                    oldPerseusId = "";
                }
                EditionItem edition = new EditionItem(editionNode
                        .getChild(ConstantStringsContainer.DESCRIPTION_STR)
                        .getText(),
                        editionNode.getChild(ConstantStringsContainer.LABEL_STR)
                                .getText(), language, work, editionNode
                        .getAttribute(ConstantStringsContainer.URN_STR),
                        hasMappingInfo, mappingString, oldPerseusId);
                editions.add(edition);
            }
        }
    }

    private void parseHerWorks(XmlNode authorNode, Author author,
                               List<Work> works, List<EditionItem> editions)
            throws XmlNode.XmlNodeException {
        int numberOfHerWorks = authorNode.getChildrenSize();
        for (int j = 0; j < numberOfHerWorks; j++) {
            XmlNode workNode = authorNode.getChild(j);
            if (workNode.getName().equals(ConstantStringsContainer.WORK_STR)) {
                Work work = new Work(
                        workNode.getChild(ConstantStringsContainer.TITLE_STR)
                                .getText(), author,
                        workNode.getAttribute(ConstantStringsContainer.URN_STR),
                        Language.fromAbbreviation(workNode.getAttribute(
                                ConstantStringsContainer.LANG_STR)));
                works.add(work);
                parseHerEditions(workNode, work, editions);
            }
        }
    }

    public void storeCapabilitiesInDb()
            throws IOException, XmlPullParserException,
            XmlNode.XmlNodeException {
        XmlNode parsedCapabilities = this.getCapabilitiesFromPerseus();
        int numberOfTextgroups = parsedCapabilities.getChildrenSize();
        List<Author> authors = new LinkedList<>();
        List<Work> works = new LinkedList<>();
        List<EditionItem> editions = new LinkedList<>();
        new Delete().from(EditionItem.class).execute();
        new Delete().from(Work.class).execute();
        new Delete().from(Author.class).execute();
        for (int i = 0; i < numberOfTextgroups; i++) {
            XmlNode authorNode = parsedCapabilities.getChild(i);
            if (authorNode.getName()
                    .equals(ConstantStringsContainer.TEXTGROUP_STR)) {
                Author author = new Author(authorNode
                        .getChild(ConstantStringsContainer.GROUPNAME_STR)
                        .getText());
                parseHerWorks(authorNode, author, works, editions);
                authors.add(author);
            }
        }
        loadDataToDb(authors, works, editions);
    }

    @Override
    public List<Author> getAuthors(String name) {
        return new Select().from(Author.class)
                .where("name " + "LIKE ?", "%" + name + "%").execute();
    }

    @Override
    public List<Work> getWorks(Author author) {
        return new Select().from(Work.class).where("author = ?", author.getId())
                .execute();
    }

    @Override
    public XmlNode getValidReffFromPerseus(String urn)
            throws IOException, XmlPullParserException {
        InputStream stream =
                getXmlFromPerseus(CTS_GETVALIDREFF_ADDR_PREF + urn);
        XmlParser parser = new XmlParser();
        return parser.parse(stream);
    }

    @Override
    public String getTextChunk(String chunkUrn, EditionItem editionItem)
            throws IOException {
        String url;
        boolean askOldPerseusInstance = editionItem.hasMappingInfo &&
                chunkUrn.startsWith(editionItem.work.urn);
        if (askOldPerseusInstance) {
            try {
                url = createOldPerseusUrl(chunkUrn, editionItem);
            } catch (IllegalArgumentException e) {
                // unfortunately there is another bug in CTS and gave us not
                // appropriate answer
                askOldPerseusInstance = false;
                url = createCTSGetPassageUrl(chunkUrn, editionItem);
            }
        } else {
            url = createCTSGetPassageUrl(chunkUrn, editionItem);
        }
        InputStream inputStream = getXmlFromPerseus(url);
        // Parsing xml with xml parsers is very difficult, because there is
        // no demarcation between xml and html, so parsing like just sting
        String inputStreamString =
                new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
        Log.d("getTextChunk", inputStreamString);
        String bodyOfAnswer;
        if (askOldPerseusInstance) {
            bodyOfAnswer = getBodyOfAnswerOldFormat(inputStreamString);
        } else {
            bodyOfAnswer = getBodyOfAnswerNewFormat(inputStreamString);
        }
        return bodyOfAnswer;
    }

    private String getBodyOfAnswerNewFormat(String tree) {
        return tree.split("<tei:body>")[1].split("</tei:body>")[0];
    }

    private String getBodyOfAnswerOldFormat(String tree) {
        Log.d("getBodyOfAnswerOld", tree.split("<body>")[1]);
        return tree.split("<body>")[1].split("</body>")[0];

    }

//    private String recreateHtmlFromParsedTree(XmlNode tree) {
//        String answer = "<" + tree.getName() + ">";
//        int numberOfChildren = tree.getChildrenSize();
//        if (numberOfChildren > 0) {
//            for (int i = 0; i < numberOfChildren; i++) {
//                answer += recreateHtmlFromParsedTree(tree.getChild(i));
//            }
//        } else {
//            answer += tree.getText();
//        }
//        return answer + "</" + tree.getName() + ">";
//    }

    public static String[] getUrnSuffix(String chunkUrn,
                               EditionItem editionItem) {
        String[] editionUrnSuffix = chunkUrn.split(editionItem.urn + ":");
        return editionUrnSuffix[1].split("\\.");
    }

    private String createOldPerseusUrl(String chunkUrn,
                                       EditionItem editionItem) {
        String url = OLD_PERSEUS_GETPASSAGE_ADDR_PREF + editionItem.xmlDocname;

        String textNumber =
                String.format(editionItem.mappingInfo, getUrnSuffix(chunkUrn,
                 editionItem       ));
        // TODO write smth here!
        return url + textNumber;
    }

    private String createCTSGetPassageUrl(String chunkUrn,
                                          EditionItem editionItem) {
        return CST_GETPASSAGE_ADDR_PREF + chunkUrn;
    }

    private class ConstantStringsContainer {
        static final String TEXTGROUP_STR = "textgroup";
        static final String GROUPNAME_STR = "groupname";
        static final String WORK_STR = "work";
        static final String LABEL_STR = "label";
        static final String LANG_STR = "xml:lang";
        static final String TRANSLATION_STR = "translation";
        static final String DESCRIPTION_STR = "description";
        static final String TITLE_STR = "title";
        static final String URN_STR = "urn";
        static final String EDITION_STR = "edition";
        static final String XML_EXTENSION_STR = ".xml";
        static final String CITATION_STR = "citation";
        static final String CIT_MAPPING_STR = CITATION_STR + "Mapping";
    }
}
