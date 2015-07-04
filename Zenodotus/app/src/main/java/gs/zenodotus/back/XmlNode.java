package gs.zenodotus.back;

import java.util.ArrayList;

/**
 * Class that represents data extracted from gDE response.
 * <p/>
 * XmlNode comes in two types - one, text node, that contains not-null "text"
 * field,
 * and the other one, inner node, which has no text, but can have children
 * nodes.
 * Both types have "name" - tag between <>
 */
public class XmlNode {
    private String name;
    private String text;
    private ArrayList<XmlNode> children;

    /**
     * Constructor for nodes with children.
     *
     * @param name     tag name
     * @param children list of child nodes
     */
    public XmlNode(String name, ArrayList<XmlNode> children) {
        this.name = name;
        this.text = null;
        this.children = children;
    }

    /**
     * Constructor for nodes with value and empty nodes
     * .
     *
     * @param name tag name
     * @param text tag value or empty string for empty nodes
     */
    public XmlNode(String name, String text) {
        this.name = name;
        this.text = text;
        this.children = null;
    }

    /**
     * Get name of this node.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get value of this node.
     *
     * @return text value
     */
    public String getText() {
        return text;
    }

    /**
     * Get count of children for this node.
     *
     * @return count of children for this node
     */
    public int getChildrenSize() {
        if (children != null) {
            return children.size();
        }
        return 0;
    }

    /**
     * Check if node has at least one child node.
     *
     * @return true if node has at least one child
     */
    public boolean hasAnyChildren() {
        return (children != null && children.size() > 0);
    }

    /**
     * Check if node has a child with specific name.
     *
     * @param childName name of child you are looking for
     * @return true if child exists, false otherwise
     */
    public boolean hasChild(String childName) {
        if (children == null) {
            return true;
        }
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getName().equals(childName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns child of this node.
     *
     * @param childName name of child to be returned
     * @return child XmlNode
     * @throws Exception if child of specified name does not exist
     */
    public XmlNode getChild(String childName) {
        try {
            int i = 0;
            while (i < children.size() &&
                    !children.get(i).getName().equals(childName)) {
                i++;
            }
            return children.get(i);
        } catch (Exception e) {
            System.out
                    .println("Call to non-existent child in getChild(String)!");
        }
        return null;
    }

    /**
     * Returns child of this node.
     *
     * @param index index of child to be returned
     * @return child XmlNode
     * @throws Exception if index of child is out of bounds
     */
    public XmlNode getChild(int index) {
        try {
            return children.get(index);
        } catch (Exception e) {
            System.out.println("Call to non-existent child in getChild(int)!");
        }
        return null;
    }
}
