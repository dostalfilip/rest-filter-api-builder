package dostal.api.filter.exception;

public class InvalidOperationException extends RuntimeException {

    private static final long serialVersionUID = 8951222008201245892L;
    
    
    public InvalidOperationException() {
        super();
    }

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidOperationException(Throwable cause) {
        super(cause);
    }
}
