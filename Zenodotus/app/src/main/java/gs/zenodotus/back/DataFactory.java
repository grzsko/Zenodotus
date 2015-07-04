package gs.zenodotus.back;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

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

    public abstract XmlNode getCapabilitiesFromPerseus();

}
