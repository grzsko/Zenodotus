package gs.zenodotus.back;


import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XmlParser {
    private static final String ns = null;
    private XmlPullParser parser;

    /**
     * Set parser to next START_TAG.
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void goToNextStartTag() throws XmlPullParserException, IOException {
        do {
            parser.next();
        } while (parser.getEventType() != XmlPullParser.START_TAG);
    }

    /**
     * Parses XML document and returns tree structure which represents that
     * document.
     *
     * @param in XML document
     * @return tree structure - {@link XmlNode}
     * @throws XmlPullParserException when XML Parser has problems with document
     * @throws IOException
     */
    public XmlNode parse(InputStream in)
            throws XmlPullParserException, IOException {
        try {
            parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            goToNextStartTag();

            String mainName = parser.getName();

            ArrayList<XmlNode> currentList = new ArrayList<XmlNode>();
            goToNextStartTag();
            while (parser.getEventType() != XmlPullParser.END_TAG) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    currentList.add(getXmlNode());
                }
                parser.next();
            }
            parser.require(XmlPullParser.END_TAG, ns, mainName);
            return new XmlNode(mainName, currentList);
        } finally {
            in.close();
        }
    }

    /**
     * Parses and returns single node (possibly with its children or value).
     * <p/>
     * Parser has to be set at START_TAG of the node to be processed.
     * Parser is set to END_TAG of the node afterwards.
     *
     * @return XmlNode representing parsed node
     * @throws XmlPullParserException when XML Parser has problems with document
     * @throws IOException
     */
    private XmlNode getXmlNode() throws XmlPullParserException, IOException {
        String name = parser.getName().toString();
        // START_TAG
        parser.require(XmlPullParser.START_TAG, ns, name);
        // Find next tag (ignore whitespace)
        do {
            parser.next();
        } while (parser.getEventType() == XmlPullParser.TEXT &&
                parser.isWhitespace());

        if (parser.getEventType() == XmlPullParser.TEXT)
        // Next tag is TEXT
        {
            return getXmlTextNode(parser, name);
        } else if (parser.getEventType() == XmlPullParser.END_TAG) {
            // Next tag is END_TAG - empty node
            return new XmlNode(name, "");
        } else if (parser.getEventType() == XmlPullParser.START_TAG) {
            // Next tag is START_TAG - meaning child nodes
            ArrayList<XmlNode> currentList = new ArrayList<XmlNode>();
            while (parser.getEventType() != XmlPullParser.END_TAG) {
                // Add node to children
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    currentList.add(getXmlNode());
                }
                parser.next();
            }
            // END_TAG - no more children
            parser.require(XmlPullParser.END_TAG, ns, name);
            return new XmlNode(name, currentList);
        }
        System.out.println("Error occurred");
        return null;
    }

    /**
     * Parses and returns single text node.
     * <p/>
     * Parser has to be set at TEXT tag of the node to be processed.
     * Parser is set to END_TAG of the node afterwards.
     *
     * @param parser parser set at the beginning of node to be processed
     * @param name   tag name
     * @return XmlNode representing parsed node
     * @throws XmlPullParserException when XML Parser has problems with document
     * @throws IOException
     */
    private XmlNode getXmlTextNode(XmlPullParser parser, String name)
            throws XmlPullParserException, IOException {
        // Get text
        String text = parser.getText();
        // Next tag - END_TAG
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, name);
        return new XmlNode(name, text);
    }
}
