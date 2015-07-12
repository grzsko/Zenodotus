package gs.zenodotus.back.commands;


public class PerseusProblemException extends Exception {
    public PerseusProblemException() {
        super();
    }

    public PerseusProblemException(String message) {
        super(message);
    }

    public PerseusProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public PerseusProblemException(Throwable cause) {
        super(cause);
    }
}
