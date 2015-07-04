package gs.zenodotus.back;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
    public XmlNode getCapabilitiesFromPerseus() {
        // TODO check if internet connection is available
        // https://developer.android.com/training/basics/network-ops/connecting.html#connection
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

    public void storeCapabilitiesInDb(XmlNode parsedCapabilities) {
    }


}
