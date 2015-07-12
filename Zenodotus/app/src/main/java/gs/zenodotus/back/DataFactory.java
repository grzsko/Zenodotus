package gs.zenodotus.back;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.EditionItem;
import gs.zenodotus.back.database.Work;
import gs.zenodotus.back.xml.XmlNode;

/**
 * Factory that provides data fetched online from Perseus or cached in db.
 * <p/>
 * Possible classes that extend this abstract class: {@link gs.zenodotus.back
 * .OnlineDataFactory}. In future possibly another offline,
 * test factory will be created.
 *
 * @author grzsko
 */
public abstract class DataFactory {
    protected XmlNode parseXml(InputStream inputStream) {
        return null;
    }

    protected abstract InputStream getXmlFromPerseus(String url)
            throws IOException;

    protected abstract XmlNode getCapabilitiesFromPerseus()
            throws IOException, XmlPullParserException;

    public abstract void storeCapabilitiesInDb()
            throws IOException, XmlPullParserException,
            XmlNode.XmlNodeException;

    public abstract List<Author> getAuthors(String name);

    public abstract List<Work> getWorks(Author author);

    public abstract XmlNode getValidReffFromPerseus(String urn)
            throws IOException, XmlPullParserException;

    public abstract String getTextChunk(String chunkUrn,
                                        EditionItem editionItem)
            throws IOException;
}
