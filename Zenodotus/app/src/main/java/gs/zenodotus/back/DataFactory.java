package gs.zenodotus.back;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import gs.zenodotus.back.database.Author;
import gs.zenodotus.back.database.Work;

/**
 * Factory that provides data fetched online from Perseus or cached in db.
 *
 * Possible classes that extend this abstract class: {@link gs.zenodotus.back
 * .OnlineDataFactory}. In future possibly another offline,
 * test factory will be created.
 *
 * @author grzsko
 *
 */
public abstract class DataFactory {
    protected XmlNode parseXml(InputStream inputStream) {
        return null;
    }
    protected abstract InputStream getXmlFromPerseus(String url)
            throws IOException;

    protected abstract XmlNode getCapabilitiesFromPerseus();

    public abstract void storeCapabilitiesInDb();

    abstract List<Author> getAuthors(String name);

    public abstract List<Work> getWorks(Author author);
}
