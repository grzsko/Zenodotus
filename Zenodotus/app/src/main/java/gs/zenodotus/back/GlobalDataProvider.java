package gs.zenodotus.back;

/**
 * Stores global attributes and methods for application.
 * There is no instance of this class, all methods are static.
 */
public class GlobalDataProvider {
    /** Instance of one data factory in all app. */
    private static OnlineDataFactory factory;

    /**
     * Creates new factory if it doesn't exist.
     */
    public static void setFactory() {
        if (factory == null) {
            factory = new OnlineDataFactory();
        }
    }

    /**
     * Returns existing factory instace.
     *
     * @return all app one factory instance
     */
    public static DataFactory getFactory() {
        setFactory();
        return factory;
    }

}
