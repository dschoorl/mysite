package info.rsdev.mysite.writing.exceptions;

public class InconsistentDocumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InconsistentDocumentException(String message) {
        super(message);
    }
}
